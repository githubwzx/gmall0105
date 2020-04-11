package com.example.gmall.cart.service.impl;

import com.alibaba.fastjson.JSON;

import com.example.gmall.bean.OmsCartItem;
import com.example.gmall.cart.mapper.OmsCartItemMapper;
import com.example.gmall.service.CartService;
import com.example.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OmsCartItemMapper omsCartItemMapper;

    @Override
    public OmsCartItem ifCartExistByUser(String memberId, String skuId) {

        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        OmsCartItem omsCartItem1 = omsCartItemMapper.selectOne(omsCartItem);
        return omsCartItem1;

    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {
        System.out.println(omsCartItem.getQuantity());
        if (StringUtils.isNotBlank(omsCartItem.getMemberId())) {
            omsCartItemMapper.insertSelective(omsCartItem);//避免添加空值
        }
    }

    @Override
    public void updateCart(OmsCartItem omsCartItemFromDb) {

        Example e = new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("id",omsCartItemFromDb.getId());

        omsCartItemMapper.updateByExampleSelective(omsCartItemFromDb,e);

    }

    @Override
    public void flushCartCache(String memberId) {//同步缓存

        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        List<OmsCartItem> omsCartItems = omsCartItemMapper.select(omsCartItem);

        // 同步到redis缓存中
        Jedis jedis = redisUtil.getJedis();

        Map<String,String> map = new HashMap<>();
        for (OmsCartItem cartItem : omsCartItems) {
            cartItem.setTotalPrice(cartItem.getPrice().multiply(cartItem.getQuantity()));
            map.put(cartItem.getProductSkuId(), JSON.toJSONString(cartItem));
        }

        jedis.del("user:"+memberId+":cart");//先删后更新 不然缓存里会多(如 5—>4,不删，第5个key 还在那)
        jedis.hmset("user:"+memberId+":cart",map);

        jedis.close();
    }

    @Override
    public List<OmsCartItem> cartList(String userId) {//从缓存取出
        Jedis jedis = null;
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        OmsCartItem omsCartItem = new OmsCartItem();
        try {
             jedis = redisUtil.getJedis();

            List<String> hvals = jedis.hvals("user:" + userId + ":cart");
            if(hvals.size()<=0||"".equals(hvals)){//缓存里没有
                //查询db
                // 设置分布式锁
                String token = UUID.randomUUID().toString();
                String lock = jedis.set("cartList:" + userId + ":lock", token, "nx", "px", 1000*5);
                Map<String,String> map = new HashMap<>();

                if(StringUtils.isNotBlank(lock)&&lock.equals("OK")){//查询db
                     omsCartItem = new OmsCartItem();
                    omsCartItem.setMemberId(userId);
                     omsCartItems = omsCartItemMapper.select(omsCartItem);
                    if(omsCartItems!=null&&!("".equals(omsCartItems))&&omsCartItems.size()>0){//查询结果存入redis
                        for (OmsCartItem cartItem : omsCartItems) {
                            map.put(cartItem.getProductSkuId(), JSON.toJSONString(cartItem));
                        }
                        jedis.hmset("user:"+userId+":cart",map);
                    }else {
                        // 为了防止缓存穿透将，null或者空字符串值设置给redis
                        omsCartItem =new OmsCartItem();
                        omsCartItem.setIsChecked("0");
                        omsCartItem.setQuantity(new BigDecimal("0"));
                        omsCartItem.setProductName("");
                        omsCartItem.setPrice(new BigDecimal("0"));
                        omsCartItem.setTotalPrice(new BigDecimal("0"));

                        omsCartItems.add(omsCartItem);
                        map.put("",JSON.toJSONString(omsCartItem));
                        jedis.hmset("user:"+userId+":cart",map);
                    }
                    //可与用lua脚本，在查询到key的同时删除该key(将分布式锁释放)，防止高并发下的意外的发生
                    String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    jedis.eval(script, Collections.singletonList("cartList:" + userId + ":lock"),Collections.singletonList(token));
                }else {//获取锁失败，自旋
                    Thread.sleep(3000);
                    return cartList(userId);
                }
            }else {//缓存里有
                for (String hval : hvals) {
                     omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                    omsCartItems.add(omsCartItem);
                }
            }

        }catch (Exception e){
            // 处理异常，记录系统日志
            e.printStackTrace();
            //String message = e.getMessage();
            //logService.addErrLog(message);
            return null;
        }finally {
            jedis.close();
        }

        return omsCartItems;
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {

        Example e = new Example(OmsCartItem.class);

        e.createCriteria().andEqualTo("memberId",omsCartItem.getMemberId()).andEqualTo("productSkuId",omsCartItem.getProductSkuId());

        omsCartItemMapper.updateByExampleSelective(omsCartItem,e);

        // 缓存同步
        flushCartCache(omsCartItem.getMemberId());

    }
}
