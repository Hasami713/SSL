package com.example.ssl.dto;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Data
@Builder
public class TaskRunnerRequest {
    private Long chatId;
    private String laundryId1;
    private InlineKeyboardMarkup inlineKeyboardMarkup;
}
