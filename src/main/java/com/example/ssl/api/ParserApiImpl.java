package com.example.ssl.api;

import com.example.ssl.config.ParserConfig;
import com.example.ssl.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ParserApiImpl implements ParserApi {

    private static final String LAUNDRIES_PATH = "api/v1/laundries";
    private static final String LAUNDRY_PATH = "api/v1/laundry";

    private final ParserConfig parserConfig;
    private final RestTemplate parserClient;

    @Override
    public LaundriesInfo getLaundries() {
        return getRequest(LAUNDRIES_PATH, null, LaundriesInfo.class);
    }

    @Override
    public Laundry getLaundry(String laundryId) {
        return getRequest(String.format("%s/%s", LAUNDRY_PATH, laundryId), null, Laundry.class);
    }

    @Override
    public LaundryInfo getLaundryInfo(String laundryId) {
        return getLaundries().stream()
                .filter(laundry -> laundry.getLaundryId().equals(laundryId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Boolean checkSlot(String laundryId, String slotId) {
        var params = new LinkedMultiValueMap<String, String>();
        params.put("slotId", Collections.singletonList(slotId));
        return getRequest(String.format("%s/%s/check", LAUNDRY_PATH, laundryId), params, Boolean.class);
    }

    @Override
    public AvailableSlots getAvailable(String laundryId) {
        return getRequest(String.format("%s/%s/available", LAUNDRY_PATH, laundryId), null, AvailableSlots.class);
    }

    private <T, Z> Z postRequest(String path, MultiValueMap<String, String> params, T body, Class<Z> clazz) {
        return parserClient.getForObject(buildUrl(path, params), clazz);
    }

    private <T> T getRequest(String path, MultiValueMap<String, String> params, Class<T> clazz) {
        return parserClient.getForObject(buildUrl(path, params), clazz);
    }

    private URI buildUrl(String path, MultiValueMap<String, String> params) {
        return UriComponentsBuilder
                .fromUriString(parserConfig.url()).path(path)
                .queryParams(params)
                .encode()
                .build()
                .toUri();
    }
}