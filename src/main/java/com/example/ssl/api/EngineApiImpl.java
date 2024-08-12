package com.example.ssl.api;

import com.example.ssl.config.ClientConfig;
import com.example.ssl.config.EngineConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class EngineApiImpl extends BaseApi implements EngineApi{

    private static final String HEARTBEAT_TEST = "api/v1/heartbeatTest";


    public EngineApiImpl(RestTemplate engineClient, EngineConfig engineConfig) {
        super(engineClient, engineConfig);
    }

    @Override
    public void heartbeatTest(String text) {
        postRequest(HEARTBEAT_TEST, null, text, String.class);
    }
}
