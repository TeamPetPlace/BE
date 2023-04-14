package com.sparta.petplace.chat.controller;

import com.sparta.petplace.auth.security.UserDetailsImpl;
import com.sparta.petplace.chat.dto.ChatRequestDto;
import com.sparta.petplace.chat.dto.ChatResponseDto;
import com.sparta.petplace.chat.dto.ChatRoomResponseDto;
import com.sparta.petplace.chat.service.ChatService;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.sse.service.NotificationService;
import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.Error;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.post.repository.PostRepository;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate template;
    private final NotificationService sseService;
    private final PostRepository postRepository;

    // 메세지보내기
    @MessageMapping("/{postId}")
    public void enter(@DestinationVariable Long postId, ChatRequestDto requestDto) {
        Member receiver = chatService.createChat(requestDto);
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new CustomException(Error.NOT_FOUND_POST);
        }
        // 채팅방에 메세지 보내기
        template.convertAndSend("/sub/" + requestDto.getRoomId(), ChatResponseDto.of(requestDto));
        sseService.send(receiver, requestDto.getSender() + "님이 새로운 채팅을 보냈어요", String.valueOf(post.get().getId()), post.get().getCategory() );
    }

    // 메세지 전체를 가져오는 메서드
    @GetMapping("/room/{roomId}")
    public ApiResponseDto<ChatRoomResponseDto> chat(@PathVariable String roomId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatService.getMessages(roomId, userDetails.getMember());
    }
}