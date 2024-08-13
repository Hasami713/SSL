package com.example.ssl.service;

import aj.org.objectweb.asm.TypeReference;
import com.example.ssl.api.EngineApi;
import com.example.ssl.api.ParserApi;
import com.example.ssl.dto.TaskRunnerRequest;
import com.example.ssl.model.*;
import com.example.ssl.repository.UserRepository;
import com.example.ssl.states.ChatState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageId;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.Collectors;

import static com.example.ssl.service.SelfServiceLaundryBot.BOT;
import static com.example.ssl.service.SelfServiceLaundryBot.sendMessage;
import static com.example.ssl.states.ChatState.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyboardMarkupService {

    private final ParserApi parserApi;
    private final UserService userService;

    private final UserRepository userRepository;

    private static InlineKeyboardMarkup getInlineKeyboardMarkup(int pageNumber, List<KeyboardButton> keyboardData, int totalPages, int pageSize) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (KeyboardButton keyboardButton : keyboardData) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(keyboardButton.text());
            button.setCallbackData(keyboardButton.callback());
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
        }

        // Добавление кнопок навигации
        if (totalPages > pageSize) {
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

        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(pageNumber, pageAddresses, totalPages, pageSize);
        if (callbackQuery == null) {
            SelfServiceLaundryBot.sendMessage(chatId, "Выберите адрес общежития:", inlineKeyboardMarkup);
        } else if (callbackQuery.getData().startsWith("BACK_TO_ADDRESSES")) {
            Integer messageId = callbackQuery.getMessage().getMessageId();
            SelfServiceLaundryBot.editMessage(chatId, messageId, "Выберите адрес общежития:", inlineKeyboardMarkup);
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
            userService.updateUserChatState(message, ADDRESS_CHOSEN);
            if (laundryInfo == null) {
                sendMessage(chatId, "Прачечная не найдена!", null);
                return;
            }
            userService.updateLaundryId(message, laundryId);
            // Обработка выбора адреса
            List<KeyboardButton> chosenAddressMenu = new ArrayList<>();
            chosenAddressMenu.add(new KeyboardButton("SUBSCRIBE_TO_ALL", "Отслеживать свободные слоты"));
            chosenAddressMenu.add(new KeyboardButton("SUBSCRIBE_TO_SLOT", "Отслеживать определенный слот"));
            chosenAddressMenu.add(new KeyboardButton("BACK_TO_ADDRESSES", "Назад⬅⬅⬅"));
            InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(1, chosenAddressMenu, 1, 8);
            SelfServiceLaundryBot.editMessage(chatId, messageId, "Вы выбрали адрес: " + laundryInfo.getAddress(), inlineKeyboardMarkup);
        } else if (data.startsWith("PAGE:")) {
            int pageNumber = Integer.parseInt(data.substring("PAGE:".length()));
            showAddresses(chatId, pageNumber, callbackQuery);
        } else if (data.startsWith("BACK_TO_ADDRESSES")) {
            showAddresses(chatId, 1, callbackQuery);
            userService.updateUserChatState(message, CHOICE_ADDRESS);
            userService.updateLaundryId(message, "-");
        } else if (data.startsWith("SUBSCRIBE_TO_ALL")) {
            //userService.updateUserChatState(message, SUBSCRIBED_TO_ALL);
            List<KeyboardButton> backToMenu = new ArrayList<>();
            backToMenu.add(new KeyboardButton("BACK_TO_MENU", "Отменить подписку⬅⬅⬅"));
            InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(1, backToMenu, 1, 8);
            while (true) {
                AvailableSlots allSlots = parserApi.getAvailable(userRepository.findById(message.getChatId()).get().getLaundryId());
                List<String> availableId = allSlots.stream().filter(value -> value.equals("1")).toList();
                if (allSlots.contains(1)) {
                    sendMessage(chatId, "Свободные слоты: " + availableId, inlineKeyboardMarkup);
                } else {
                    sendMessage(chatId, "Нет свободных слотов(", inlineKeyboardMarkup);
                }
            }
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
