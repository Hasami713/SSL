package com.example.ssl.model;

import com.example.ssl.states.ChatState;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    private ChatState state;
    private String laundryId;
}
