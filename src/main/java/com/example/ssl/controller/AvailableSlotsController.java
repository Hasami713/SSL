package com.example.ssl.controller;

import com.example.ssl.api.ParserApiImpl;
import com.example.ssl.dto.SendMessageRequest;
import com.example.ssl.dto.SendMessageResponse;
import com.example.ssl.dto.SendState;
import com.example.ssl.model.TelegramUser;
import com.example.ssl.repository.UserRepository;
import com.example.ssl.service.SelfServiceLaundryBot;
import com.example.ssl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.telegram.telegrambots.meta.api.objects.ChatInviteLink;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

import static com.example.ssl.service.SelfServiceLaundryBot.*;

@Controller
@RequiredArgsConstructor
public class AvailableSlotsController {
    @PostMapping("api/v1/availableSlots")
    public static @ResponseBody SendMessageResponse sendMessage(@RequestBody SendMessageRequest sendMessageRequest) {
        try {
            SelfServiceLaundryBot.sendMessage(sendMessageRequest.getChatId(),
                    sendMessageRequest.getTextToSend(),
                    sendMessageRequest.getInlineKeyboardMarkup());
        } catch (Exception e) {
            if (e.getMessage().contains("Too Many Requests")) {
                return SendMessageResponse.builder()
                        .sendState(SendState.ERROR_TOO_MANY_REQUESTS)
                        .build();
            }
        }
        return SendMessageResponse.builder()
                .sendState(SendState.SUCCESS)
                .build();
    }
}
