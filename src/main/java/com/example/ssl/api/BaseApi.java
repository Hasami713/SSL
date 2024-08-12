package com.example.ssl.api;

import com.example.ssl.config.ClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


@Slf4j
@RequiredArgsConstructor
public class BaseApi {
    private final RestTemplate restTemplate;
    private final ClientConfig config;

    protected <T, Z> Z postRequest(String path, MultiValueMap<String, String> params, T body, Class<Z> clazz) {
        return restTemplate.postForObject(buildUrl(path, params), body, clazz);
    }

    protected <T> T getRequest(String path, MultiValueMap<String, String> params, Class<T> clazz) {
        return restTemplate.getForObject(buildUrl(path, params), clazz);
    }

    protected URI buildUrl(String path, MultiValueMap<String, String> params) {
        return UriComponentsBuilder
                .fromUriString(config.getUrl()).path(path)
                .queryParams(params)
                .encode()
                .build()
                .toUri();
    }
}
