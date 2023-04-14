package com.sparta.petplace.chat.dto;

import com.sparta.petplace.chat.entity.ChatRoom;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ChatRoomResponseDto {
    private List<ChatResponseDto> messages;
    private String roomName;

    public static ChatRoomResponseDto of(ChatRoom room) {

        return ChatRoomResponseDto.builder()
                .messages(room.getChatMessages().stream().map(ChatResponseDto::of).toList())
                .roomName(room.getPost().getCategory())
                .build();
    }
}
