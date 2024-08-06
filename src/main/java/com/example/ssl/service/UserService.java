package com.example.ssl.service;

import com.example.ssl.model.TelegramUser;
import com.example.ssl.repository.UserRepository;
import com.example.ssl.states.ChatState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.CloneUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;
import java.util.Optional;

import static com.example.ssl.states.ChatState.CHOICE_ADDRESS;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public void registerUser(Message message) {
        if (userRepository.findById(message.getChatId()).isEmpty()) {
            Long chatId = message.getChatId();
            Chat chat = message.getChat();
            TelegramUser user = new TelegramUser();
            user.setId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setMessageId(message.getMessageId());
            user.setUserName(chat.getUserName());
            user.setLaundryId("-");
            user.setState(CHOICE_ADDRESS);
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
            log.info("Telegram user saved: " + user);
        }
    }

    public void updateUserChatState(Message message, ChatState chatState) {
        Optional<TelegramUser> user = userRepository.findById(message.getChatId());
        if(user.isPresent()) {
            user.get().setState(chatState);
            userRepository.save(user.get());
            log.info("Chat state updated : " + chatState);
        } else {
            log.error("Chat state not updated.");
        }
    }

    public void updateLaundryId(Message message, String laundryId) {
        Optional<TelegramUser> user = userRepository.findById(message.getChatId());
        if(user.isPresent()) {
            user.get().setLaundryId(laundryId);
            userRepository.save(user.get());
            log.info("Laundry id updated : " + laundryId);
        } else {
            log.error("Laundry id not updated.");
        }
    }
}
