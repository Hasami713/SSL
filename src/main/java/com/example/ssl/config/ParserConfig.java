package com.example.ssl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@ConfigurationProperties(prefix = "parser")
public record ParserConfig(String url) {

    @Bean
    public RestTemplate parserClient() {
        return new RestTemplate();
    }
}
