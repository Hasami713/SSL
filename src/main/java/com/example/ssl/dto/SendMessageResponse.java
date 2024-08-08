package com.example.ssl.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendMessageResponse {
    private final SendState sendState;
}
