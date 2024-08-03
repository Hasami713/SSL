package com.example.ssl.repository;

import com.example.ssl.model.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<TelegramUser, Long> {
    Optional<TelegramUser> findById(Long id);
 }
