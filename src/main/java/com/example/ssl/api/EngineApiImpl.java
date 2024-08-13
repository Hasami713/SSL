package com.example.ssl.api;

import com.example.ssl.config.ClientConfig;
import com.example.ssl.config.EngineConfig;
import com.example.ssl.dto.TaskRunnerRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class EngineApiImpl extends BaseApi implements EngineApi{

    private static final String HEARTBEAT_TEST = "/api/v1/taskRunner";


    public EngineApiImpl(RestTemplate engineClient, EngineConfig engineConfig) {
        super(engineClient, engineConfig);
    }

    @Override
    public void taskRunner(TaskRunnerRequest taskRunnerRequest) {
        postRequest(HEARTBEAT_TEST, null, taskRunnerRequest, String.class);
    }
}
