package com.sparta.petplace.chat.dto;

import com.sparta.petplace.chat.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class ChatResponseDto {
    private String sender;
    private String message;
    private String chatTime;

    public static ChatResponseDto of(ChatRequestDto requestDto) {
        return ChatResponseDto.builder()
                .sender(requestDto.getSender())
                .message(requestDto.getMessage())
                .chatTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
                .build();
    }

    public static ChatResponseDto of(ChatMessage message) {
        return ChatResponseDto.builder()
                .sender(message.getSender().getNickname())
                .message(message.getMessage())
                .chatTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
                .build();
    }
}
