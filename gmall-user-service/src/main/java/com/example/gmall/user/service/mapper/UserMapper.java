package com.example.gmall.user.service.mapper;


import com.example.gmall.bean.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserMapper extends Mapper<UmsMember>{

    List<UmsMember> selectAllUser();

}
