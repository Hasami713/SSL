package com.example.ssl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot")
public record BotConfig(String name, String token) {}
