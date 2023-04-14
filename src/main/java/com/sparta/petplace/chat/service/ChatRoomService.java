package com.sparta.petplace.chat.service;

import com.sparta.petplace.chat.dto.ChatRoomListResponseDto;
import com.sparta.petplace.chat.entity.ChatMessage;
import com.sparta.petplace.chat.entity.ChatRoom;
import com.sparta.petplace.chat.repository.ChatRoomRepository;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.ResponseUtils;
import com.sparta.petplace.common.SuccessResponse;
import com.sparta.petplace.common.Time;
import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.Error;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final PostRepository postRepository;
    private final ChatRoomRepository chatRoomRepository;


    // 채팅방 생성 및 들어가기
    @Transactional
    public String createChatRoom(Long postId, Member member){
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()){
            throw new CustomException(Error.NOT_FOUND_POST);
        }
        ChatRoom room = chatRoomRepository.findChatRoomByPostIdAndGuestId(post.get().getId(), member.getId()).orElse(ChatRoom.of(post.get(),member));
        chatRoomRepository.save(room);
        readChat(room.isHost(member),room);
        return room.getRoomId();
    }


    // 채팅방 나가기
    public ApiResponseDto<SuccessResponse> exitRoom(String roomId, Member member) {
        Optional<ChatRoom> room = chatRoomRepository.findByRoomId(roomId);
        if(room.isEmpty()){
            throw new CustomException(Error.CHATROOM_NOT_FOUND);
        }
        boolean isHost = room.get().isHost(member);
        exitRoom(isHost,room.get());
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK,  " 채팅방을 나가셨습니다."));
    }

    // 채팅방 리스트 조회하기
    @Transactional(readOnly = true)
    public ApiResponseDto<List<ChatRoomListResponseDto>> getRoomList(Member member) {
        List<ChatRoom> roomList = chatRoomRepository.findAllByHostOrGuestOrderByModifiedAtDesc(member, member);
        List<ChatRoomListResponseDto> chatRoomListDto = new ArrayList<>();
        for (ChatRoom room : roomList) {
            boolean isHost = room.isHost(member);
            if (isLeaved(isHost, room)) {
                continue;
            }
            ChatRoomListResponseDto roomBuilder = ChatRoomListResponseDto.of(room, getPartner(isHost, room));
            if (!room.getChatMessages().isEmpty()) {
                ChatMessage message = room.getChatMessages().get(room.getChatMessages().size() - 1); // 마지막 메시지를 들고온다 인덱스라 -1
                String lastChat = message.getMessage();
                String time = Time.chatTime(message.getCreatedAt());
                roomBuilder.setLastChat(lastChat, time);
            }
            chatRoomListDto.add(roomBuilder);
        }
            return ResponseUtils.ok(chatRoomListDto);
    }


//    ============================= Method ================================
    private void exitRoom(boolean isHost, ChatRoom room) {
        if (isHost) {
            room.setHostExited(true);
        } else {
            room.setGuestExited(true);
        }
        if (room.isHostExited() && room.isGuestExited()) {
            chatRoomRepository.deleteById(room.getId());
        }
    }


    private void readChat(boolean isHost, ChatRoom room) {
        if (isHost) {
            room.initGuestChatCount();
        } else {
            room.initHostChatCount();
        }
    }

    private boolean isLeaved(boolean isHost, ChatRoom room) {
        return (isHost && room.isHostExited()) ||
                (!isHost && room.isGuestExited());
    }

    private int getUnreadChat(boolean isHost, ChatRoom room) {
        return isHost ? room.getGuestChatCount() : room.getHostChatCount();
    }


    private Member getPartner(boolean isHost, ChatRoom room) {
        return isHost ? room.getGuest() : room.getHost();
    }

}