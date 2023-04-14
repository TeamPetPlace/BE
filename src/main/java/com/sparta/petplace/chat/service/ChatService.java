package com.sparta.petplace.chat.service;

import com.sparta.petplace.chat.dto.ChatRequestDto;
import com.sparta.petplace.chat.dto.ChatRoomResponseDto;
import com.sparta.petplace.chat.entity.ChatMessage;
import com.sparta.petplace.chat.entity.ChatRoom;
import com.sparta.petplace.chat.repository.ChatRepository;
import com.sparta.petplace.chat.repository.ChatRoomRepository;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.ResponseUtils;
import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.Error;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Member createChat(ChatRequestDto dto) {

        ChatRoom room = chatRoomRepository.findByRoomId(dto.getRoomId()).orElseThrow(
                () -> new CustomException(Error.CHATROOM_NOT_FOUND)
        );
        log.info("room.getRoomId = "+room.getRoomId());
        Member sender = memberRepository.findByNickname(dto.getSender()).orElseThrow(
                () -> new CustomException(Error.NO_AUTHORITY)
        );
        log.info("sender = "+sender);
        boolean isHost = room.isHost(sender); //접속자(sender가 Host인지 확인) host = true

        ChatMessage message = ChatMessage.of(dto, room, sender);  //chat 인스턴스 생성
        chatRepository.save(message); // 데이터 저장
        setChatCount(isHost, room); // isHost가 true면 ChatRoom에서 HostChatCout를 +1 해준다 아니면 GuestChatCount를 +1
        reEnterRoom(isHost, room); // isHost가 true면 GuestExited를 false로 아니면 반대로
        return getPartner(isHost, room); // isHost가 true면 ChatRoom에 등록된Member host를 반환 아니면 Member guest반환
    }
        // 메세지 List를 가져오는 메서드
        @Transactional
        public ApiResponseDto<ChatRoomResponseDto> getMessages(String roomId, Member member) {
            ChatRoom room = chatRoomRepository.findByRoomId(roomId).orElseThrow(() -> new CustomException(Error.NOT_FOUND_POST));
            readChat(room.isHost(member), room);
            return ResponseUtils.ok(ChatRoomResponseDto.of(room));
        }

    private void reEnterRoom(boolean isHost, ChatRoom room) {
        if (isHost) {
            room.setGuestExited(false);
        } else {
            room.setHostExited(false);
        }
    }

    private void setChatCount(boolean isHost, ChatRoom room) {
        if (isHost) {
            room.setHostChatCount();
        } else {
            room.setGuestChatCount();
        }
    }

    private void readChat(boolean isHost, ChatRoom room) {
        if (isHost) {
            room.initGuestChatCount();
        } else {
            room.initHostChatCount();
        }
    }

    private Member getPartner(boolean isHost, ChatRoom room) {
        return isHost ? room.getGuest() : room.getHost();
    }
}



