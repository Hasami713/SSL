package com.example.ssl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.ssl.service.SelfServiceLaundryBot.BOT;
import static com.example.ssl.service.SelfServiceLaundryBot.sendMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyboardMarkupService {
    public void showAddresses(Long chatId, int pageNumber, CallbackQuery callbackQuery) {
        List<String> addresses = Arrays.asList(
                "Пушкина 32а",
                "Нижнее Залупкино 69",
                "Октябрьская 10",
                "Ленина 15",
                "Советская 24",
                "Гагарина 1",
                "Московская 45",
                "Мира 77",
                "Кирова 22",
                "Карла Маркса 18"
        );

        int pageSize = 3; // Количество адресов на странице
        int totalPages = (int) Math.ceil((double) addresses.size() / pageSize);

        // Определение границ страницы
        int startIndex = (pageNumber - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, addresses.size());

        List<String> pageAddresses = addresses.subList(startIndex, endIndex);

        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(pageNumber, pageAddresses, totalPages);
        if (callbackQuery == null) {
            SelfServiceLaundryBot.sendMessage(chatId, "Выберите адрес общежития:", inlineKeyboardMarkup);
        } else {
            editKeyboard(callbackQuery, inlineKeyboardMarkup
            );
        }
    }

    private static InlineKeyboardMarkup getInlineKeyboardMarkup(int pageNumber, List<String> pageAddresses, int totalPages) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (String address : pageAddresses) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(address);
            button.setCallbackData("ADDRESS:" + address);
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
        }

        // Добавление кнопок навигации
        List<InlineKeyboardButton> navigationRow = new ArrayList<>();
        if (pageNumber > 1) {
            InlineKeyboardButton prevButton = new InlineKeyboardButton();
            prevButton.setText("⬅️ Назад");
            prevButton.setCallbackData("PAGE:" + (pageNumber - 1));
            navigationRow.add(prevButton);
        }
        if (pageNumber < totalPages) {
            InlineKeyboardButton nextButton = new InlineKeyboardButton();
            nextButton.setText("Вперед ➡️");
            nextButton.setCallbackData("PAGE:" + (pageNumber + 1));
            navigationRow.add(nextButton);
        }
        if (!navigationRow.isEmpty()) {
            rowsInline.add(navigationRow);
        }

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;
    }

    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        if (data.startsWith("ADDRESS:")) {
            String address = data.substring("ADDRESS:".length());
            // Обработка выбора адреса
            SelfServiceLaundryBot.editMessage(chatId, messageId, "Вы выбрали адрес: " + address);
            WebClient webClient =  webClient = WebClient.builder()
                    .baseUrl("http://185.223.93.175:8091/api/v1/laundry") // Базовый URL
                    .build();
            Mono<String> jsonResponse = webClient.get()
                    .uri("/pr3das5") // Добавляем относительный путь к URL
                    .retrieve()
                    .bodyToMono(String.class);
            sendMessage(chatId, jsonResponse.toString());
        } else if (data.startsWith("PAGE:")) {
            int pageNumber = Integer.parseInt(data.substring("PAGE:".length()));
            showAddresses(chatId, pageNumber, callbackQuery);
        }
    }

    public void editKeyboard(CallbackQuery callbackQuery, InlineKeyboardMarkup inlineKeyboardMarkup) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setText(callbackQuery.getMessage().getText());
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        try {
            BOT.execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
