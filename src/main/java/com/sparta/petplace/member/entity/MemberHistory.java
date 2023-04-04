package com.sparta.petplace.member.entity;

import com.sparta.petplace.post.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class MemberHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "POST_ID")
    private Post post;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Builder
    public MemberHistory(Member member, Post post, Date createdAt) {
        this.member = member;
        this.post = post;
        this.createdAt = createdAt;
    }

    public static MemberHistory of(Member member, Post posts, Date date) {
        return MemberHistory.builder()
                .member(member)
                .post(posts)
                .createdAt(date)
                .build();
    }
}
