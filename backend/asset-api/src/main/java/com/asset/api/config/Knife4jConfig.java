package com.asset.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("家庭资产监控管理平台API")
                        .version("1.0.0")
                        .description("基于多元化配置的专业资产管理解决方案")
                        .contact(new Contact()
                                .name("Asset Management Team")));
    }
}