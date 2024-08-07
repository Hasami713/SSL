package com.example.ssl.controller;

import com.example.ssl.api.ParserApiImpl;
import com.example.ssl.model.TelegramUser;
import com.example.ssl.repository.UserRepository;
import com.example.ssl.service.SelfServiceLaundryBot;
import com.example.ssl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.telegram.telegrambots.meta.api.objects.ChatInviteLink;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Optional;

import static com.example.ssl.service.SelfServiceLaundryBot.*;

@Controller
@RequiredArgsConstructor
public class AvailableSlotsController {
    @PostMapping("api/v1/availableSlots")
    public static void sendMessage(Long chatId, String textToSend, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SelfServiceLaundryBot.sendMessage(chatId, textToSend, inlineKeyboardMarkup);
    }
}
