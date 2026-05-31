package com.kset.demo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.kset.demo")
@EnableDubbo
@MapperScan({"com.kset.demo.user.mapper", "com.kset.demo.order.mapper"})
public class DemoMicroApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoMicroApplication.class, args);
    }
}
