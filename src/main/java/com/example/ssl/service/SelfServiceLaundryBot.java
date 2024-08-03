package com.example.ssl.service;

import com.example.ssl.config.BotConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SelfServiceLaundryBot extends TelegramLongPollingBot {
    static TelegramLongPollingBot BOT;

    private final BotConfig config;
    private final MessageHandlerService messageHandlerService;

    @PostConstruct
    private void init() {
        BOT = this;
    }
    @Override
    public void onUpdateReceived(Update update) {
        messageHandlerService.messageHandle(update);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    public static Message sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf((chatId)));
        message.setText(textToSend);

        try {
            return BOT.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occured:" + e.getMessage());
        }
        return null;
    }

    public static void sendMessage(Long chatId, String textToSend, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf((chatId)));
        message.setText(textToSend);
        message.setReplyMarkup(inlineKeyboardMarkup);
        try {
            BOT.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occured:" + e.getMessage());
        }
    }

    public static void editMessage(Long chatId, Integer messageId, String text) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(text);
        try {
            BOT.execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error("Error occured:" + e.getMessage());
        }
    }

}
