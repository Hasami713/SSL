package com.example.ssl.controller;

import com.example.ssl.api.ParserApiImpl;
import com.example.ssl.model.TelegramUser;
import com.example.ssl.repository.UserRepository;
import com.example.ssl.service.SelfServiceLaundryBot;
import com.example.ssl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

import static com.example.ssl.service.SelfServiceLaundryBot.*;

@Controller
@RequiredArgsConstructor
public class AvailableSlotsController {

    private final ParserApiImpl parserApi;
    private final UserRepository userRepository;

    @PostMapping("api/v1/availableSlots")
    public void sendAvailableSlots(Message message) {
        Optional<TelegramUser> user = userRepository.findById(message.getChatId());
        sendMessage(message.getChatId(), "Свободные слоты: " + parserApi.getAvailable(user.get().getLaundryId()), null);
    }
}
