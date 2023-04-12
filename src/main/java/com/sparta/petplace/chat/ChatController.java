package com.sparta.petplace.chat;

import com.sparta.petplace.auth.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("{postId}")
    public ChatRoom createRoom(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatService.createRoom(postId, userDetails.getMember());
    }

    @GetMapping
    public List<ChatRoom> findAllRoom() {
        return chatService.findAllRoom();
    }

    @MessageMapping("/enter")
    public void enterChatRoom(WebSocketSession session, ChatMessage chatMessage) {
        chatService.enterChatRoom(chatMessage.getRoomId(), session);
    }
}
