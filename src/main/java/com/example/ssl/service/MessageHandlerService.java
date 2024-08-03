package com.example.ssl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandlerService {

    private final UserService userService;
    private final KeyboardMarkupService keyboardMarkupService;

    public void messageHandle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            Long chatId = message.getChatId();
            switch (messageText) {
                case "/start":
                    keyboardMarkupService.showAddresses(chatId, 1, null);
                    break;
            }
            userService.registerUser(message);
        }  else if (update.hasCallbackQuery()) {
            keyboardMarkupService.handleCallbackQuery(update.getCallbackQuery());
        }
    }

}
