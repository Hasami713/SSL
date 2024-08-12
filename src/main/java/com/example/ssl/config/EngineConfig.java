package com.example.ssl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@ConfigurationProperties(prefix = "engine")
public class EngineConfig extends ClientConfig {

    public EngineConfig(String url) {
        super(url);
    }

    @Bean
    public RestTemplate engineClient() {
        return new RestTemplate();
    }
}
