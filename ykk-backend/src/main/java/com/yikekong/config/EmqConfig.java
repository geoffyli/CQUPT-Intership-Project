package com.yikekong.config;

import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("emq")
@Data
public class EmqConfig{
    // Config mqtt server url manually
    @Value("tcp://127.0.0.1:1883")
    private String mqttServerUrl;
}
