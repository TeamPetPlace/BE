package com.sparta.petplace.chat.repository;

import com.sparta.petplace.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository <ChatMessage ,Long >{
}
