package com.sparta.petplace.chat.controller;

import com.sparta.petplace.auth.security.UserDetailsImpl;
import com.sparta.petplace.chat.dto.ChatRoomListResponseDto;
import com.sparta.petplace.chat.service.ChatRoomService;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 나의 채팅방 조회
    @GetMapping("/rooms")
    public ApiResponseDto<List<ChatRoomListResponseDto>> rooms(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatRoomService.getRoomList(userDetails.getMember());
    }

    // 채팅방 생성, 들어가기 이미 채팅방이 있을경우
    @PostMapping("/{postId}")
    public String createChatRoom(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.createChatRoom(postId, userDetails.getMember());
    }

    // 채팅방 나가기
    @DeleteMapping("/room/exit/{roomId}")
    public ApiResponseDto<SuccessResponse> exitRoom(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.exitRoom(roomId, userDetails.getMember());
    }
}
