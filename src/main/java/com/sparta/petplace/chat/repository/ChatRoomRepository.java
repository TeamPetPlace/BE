package com.sparta.petplace.chat.repository;

import com.sparta.petplace.chat.entity.ChatRoom;
import com.sparta.petplace.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomId(String Id);

    List<ChatRoom> findAllByHostOrGuestOrderByModifiedAtDesc(Member Host, Member Guest);


    Optional<ChatRoom> findChatRoomByPostIdAndGuestId(Long postId, Long guestId);
}
