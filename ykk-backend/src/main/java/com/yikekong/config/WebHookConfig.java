package com.yikekong.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("webhook")
@Data
public class WebHookConfig {

    private String online;  //断连web钩子地址

    private String gps;  //gps钩子地址
}
