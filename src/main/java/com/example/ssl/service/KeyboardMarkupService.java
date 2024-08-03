package com.example.ssl.service;

import com.example.ssl.api.ParserApi;
import com.example.ssl.model.KeyboardButton;
import com.example.ssl.model.LaundryInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.example.ssl.service.SelfServiceLaundryBot.BOT;
import static com.example.ssl.service.SelfServiceLaundryBot.sendMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyboardMarkupService {

    private final ParserApi parserApi;

    private static InlineKeyboardMarkup getInlineKeyboardMarkup(int pageNumber, List<KeyboardButton> pageAddresses, int totalPages) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (KeyboardButton keyboardButton : pageAddresses) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(keyboardButton.text());
            button.setCallbackData(keyboardButton.callback());
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

    public void showAddresses(Long chatId, int pageNumber, CallbackQuery callbackQuery) {

        List<LaundryInfo> laundries = parserApi.getLaundries();

        List<KeyboardButton> buttons = laundries.stream()
                .map(laundry -> new KeyboardButton(
                        "LAUNDRY_ID:".concat(laundry.getLaundryId()),
                        laundry.getAddress())
                )
                .toList();

        int pageSize = 8; // Количество адресов на странице
        int totalPages = (int) Math.ceil((double) buttons.size() / pageSize);

        // Определение границ страницы
        int startIndex = (pageNumber - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, buttons.size());

        List<KeyboardButton> pageAddresses = buttons.subList(startIndex, endIndex);

        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(pageNumber, pageAddresses, totalPages);
        if (callbackQuery == null) {
            SelfServiceLaundryBot.sendMessage(chatId, "Выберите адрес общежития:", inlineKeyboardMarkup);
        } else {
            editKeyboard(callbackQuery, inlineKeyboardMarkup);
        }
    }

    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Message message = callbackQuery.getMessage();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();

        if (data.startsWith("LAUNDRY_ID:")) {
            String laundryId = data.substring("LAUNDRY_ID:".length());
            LaundryInfo laundryInfo = parserApi.getLaundryInfo(laundryId);

            if (laundryInfo == null) {
                sendMessage(chatId, "Прачечная не найдена!");
                return;
            }

            // Обработка выбора адреса
            SelfServiceLaundryBot.editMessage(chatId, messageId, "Вы выбрали адрес: " + laundryInfo.getAddress());
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
