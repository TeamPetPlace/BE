package com.sparta.petplace.like.entity;

import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.post.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID",nullable = false)
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;

    @Builder
    public Likes(Member member,Post post){
        this.member = member;
        this.post = post;
    }

    public static Likes of(Member member,Post post){
        return Likes.builder()
                .member(member)
                .post(post)
                .build();
    }
}
