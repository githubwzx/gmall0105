package com.example.gmall.manager.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.example.gmall.manager.service.mapper")
public class GmallManagerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallManagerServiceApplication.class, args);
    }

}
