package com.campusred;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.campusred.mapper")
public class CampusRedApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusRedApplication.class, args);
    }
}
