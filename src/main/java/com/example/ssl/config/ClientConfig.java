package com.example.ssl.config;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ClientConfig {
    private final String url;

}
