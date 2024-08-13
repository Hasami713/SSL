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
import java.util.List;

@Component
public class ParserApiImpl extends BaseApi implements ParserApi {

    private static final String LAUNDRIES_PATH = "api/v1/laundries";
    private static final String LAUNDRY_PATH = "api/v1/laundry";


    public ParserApiImpl(RestTemplate parserClient, ParserConfig parserConfig) {
        super(parserClient, parserConfig);
    }


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
    public Boolean checkAvailable(String laundryId, String slotId) {
        var params = new LinkedMultiValueMap<String, String>();
        params.put("slotId", Collections.singletonList(slotId));
        return getRequest(String.format("%s/%s/check", LAUNDRY_PATH, laundryId), params, Boolean.class);
    }

    @Override
    public List<Slot> getAvailable(String laundryId) {
        return getRequest(String.format("%s/%s/available", LAUNDRY_PATH, laundryId), null, AvailableSlots.class);
    }
}