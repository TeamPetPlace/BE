package com.sparta.petplace.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.Error;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final PostRepository postRepository;
    private final ObjectMapper objectMapper;
    private Map<String, ChatRoom> chatRooms;

    @PostConstruct
    private void init() {
        chatRooms = new ConcurrentHashMap<>();
    }

    public List<ChatRoom> findAllRoom() {
        return new ArrayList<>(chatRooms.values());
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }

    //채팅룸 생성
    public ChatRoom createRoom(Long postId, Member member) {
        Post p = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(Error.NOT_FOUND_POST)
        );
//        채팅방 이름 중복 확인
        Optional<ChatRoom> existingChatRoom = chatRooms.values().stream()
                .filter(room -> room.getName().equals(p.getTitle()+" 에서 "+member.getNickname()+" 님의 문의"))
                .findFirst();
        if(existingChatRoom.isPresent()){
            throw new CustomException(Error.DUPLICATED_CHATROOM);
        }
        String randomId = UUID.randomUUID().toString();
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(randomId)
                .name(p.getTitle()+" 에서 "+member.getNickname()+" 님의 문의")
                .build();
        chatRooms.put(randomId, chatRoom);
        return chatRoom;
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    // 유저를 채팅방에 입장시키는 메서드
    public void enterChatRoom(String roomId, WebSocketSession session) {
        ChatRoom chatRoom = findRoomById(roomId);
        chatRoom.getSessions().add(session);
    }
}