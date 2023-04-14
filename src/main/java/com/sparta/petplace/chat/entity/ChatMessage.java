package com.sparta.petplace.chat.entity;

import com.sparta.petplace.chat.dto.ChatRequestDto;
import com.sparta.petplace.common.Timestamped;
import com.sparta.petplace.member.entity.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ChatRoom room;

    @ManyToOne
    private Member sender;

    private String message;


    public static ChatMessage of(ChatRequestDto dto, ChatRoom room, Member sender) {
        return ChatMessage.builder()
                .room(room)
                .sender(sender)
                .message(dto.getMessage())
                .build();
    }
}