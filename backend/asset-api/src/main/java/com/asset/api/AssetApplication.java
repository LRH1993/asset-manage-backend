package com.asset.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 应用启动类
 */
@SpringBootApplication
@MapperScan("com.asset.domain.repository")
@ComponentScan(basePackages = {
    "com.asset.api",
    "com.asset.service",
    "com.asset.domain",
    "com.asset.common",
    "com.asset.integration"
})
public class AssetApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetApplication.class, args);
    }
}