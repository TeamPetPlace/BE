package com.sparta.petplace.chat.dto;

import com.sparta.petplace.chat.entity.ChatRoom;
import com.sparta.petplace.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomListResponseDto {
    private String roomId;
    private String roomName;
    private String partner;
    private String lastChat;
    private String time;
    private Long postId;
    private String postName;
    private int unreadChat;

    @Builder
    public ChatRoomListResponseDto(String roomId, String roomName, String partner, String lastChat, String time, Long postId, String postName, int unreadChat) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.partner = partner;
        this.lastChat = lastChat;
        this.time = time;
        this.postId = postId;
        this.postName = postName;
        this.unreadChat = unreadChat;
    }

    public static ChatRoomListResponseDto of(ChatRoom room, Member partner) {
        return ChatRoomListResponseDto.builder()
                .roomId(room.getRoomId())
                .roomName(room.getPost().getCategory())
                .partner(partner.getNickname())
                .postId(room.getPost().getId())
                .postName(room.getPost().getTitle())
                .build();
    }

    public void setLastChat(String content, String time) {
        this.lastChat = content;
        this.time =time;
    }
}
