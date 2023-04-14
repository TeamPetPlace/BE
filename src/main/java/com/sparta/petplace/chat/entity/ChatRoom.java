package com.sparta.petplace.chat.entity;

import com.sparta.petplace.common.Timestamped;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;
    private String name;

    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE)
    List<ChatMessage> chatMessages;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    private Member host;

    @ManyToOne(fetch = FetchType.EAGER)
    private Member guest;

    private boolean isHostExited; //호스트가 접속중인지 아닌지

    private boolean isGuestExited; // 게스트가 접속중인지 아닌지

    private int hostChatCount; // 호스트 쳇팅을 개수 칠떄마다 늘어나며 읽으면 0으로 처리한다

    private int guestChatCount; // 게스트 쳇팅을 개수 칠떄마다 늘어나며 읽으면 0으로 처리한다

    public static ChatRoom of(Post post, Member member) {
        return ChatRoom.builder()
                .roomId(UUID.randomUUID().toString()) // 고유 ID 값으로 생성
                .post(post)
                .host(post.getMember())
                .guest(member)
                .build();
    }


    public void setHostExited(boolean hostExited) {
        this.isHostExited = hostExited;
    }

    public void setGuestExited(boolean guestExited) {
        this.isGuestExited = guestExited;
    }

    public void setHostChatCount() {
        this.hostChatCount++;
    }

    public void setGuestChatCount() {
        this.guestChatCount++;
    }

    public void initHostChatCount() {
        this.hostChatCount = 0;
    }

    public void initGuestChatCount() {
        this.guestChatCount = 0;
    }

    public boolean isHost(Member member) {
        return getHost().getId().equals(member.getId());
    }
}
