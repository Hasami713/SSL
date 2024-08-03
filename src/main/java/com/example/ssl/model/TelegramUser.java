package com.example.ssl.model;

import com.example.ssl.states.ChatState;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class TelegramUser {
    @Id
    private Long id;
    private String firstName;
    private String lastName;

    private String userName;
    private Timestamp registeredAt;
    private Integer messageId;
    private ChatState state;
}
