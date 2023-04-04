package com.sparta.petplace.review.entity;

import com.sparta.petplace.common.Timestamped;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.review.dto.ReviewRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Review extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer star;

    @Column(nullable = false)
    private String review;

    @Column
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID",nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    public void update(ReviewRequestDto requestDto, String image, Member member) {
        this.star = requestDto.getStar();
        this.review = requestDto.getReview();
        this.image = image;
        this.member = member;
    }

    public Review(ReviewRequestDto reviewRequestDto, String image, Post post, Member member) {
        this.star = reviewRequestDto.getStar();
        this.review = reviewRequestDto.getReview();
        this.image = image;
        this.post = post;
        this.member = member;
    }
}
