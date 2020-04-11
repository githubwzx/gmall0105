package com.example.gmall.cart.web.controller;

import java.math.BigDecimal;

public class TestBigDecimal {

    public static void main(String[] args) {
        // 初始化(使用字符串)
        BigDecimal b1 = new BigDecimal(0.01f);
        BigDecimal b2 = new BigDecimal(0.01d);
        BigDecimal b3 = new BigDecimal("0.01");
        System.out.println(b1);
        System.out.println(b2);
        System.out.println(b3);

        // 比较
        int i = b1.compareTo(b2);// 1 0 -1  -1:小于
        System.out.println(i);

        // 运算
        BigDecimal add = b1.add(b2);//加
        System.out.println(add);

        // 约数
        BigDecimal b11 = new BigDecimal(0.01f);
        BigDecimal b22 = new BigDecimal(0.04d);
        BigDecimal subtract = b22.subtract(b11);    //减法
        BigDecimal bigDecimal = subtract.setScale(3, BigDecimal.ROUND_HALF_DOWN); //保留3位小数 四舍五入
        System.out.println(bigDecimal);

        BigDecimal b4 = new BigDecimal("6");
        BigDecimal b5 = new BigDecimal("7");
        BigDecimal multiply = b4.multiply(b5);//乘
        System.out.println(multiply);

        BigDecimal divide = b4.divide(b5,3,BigDecimal.ROUND_HALF_DOWN);//除   b4除b5 保留3位小数 四舍五入
        System.out.println(divide);



    }
}
