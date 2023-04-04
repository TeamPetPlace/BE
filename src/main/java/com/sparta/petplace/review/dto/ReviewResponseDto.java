package com.sparta.petplace.review.dto;

import com.sparta.petplace.review.entity.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private String title;
    private String category;
    private String email;
    private Integer star;
    private String review;
    private String memberImage;
    private String image;
    private String nickname;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.title =  review.getPost().getTitle();
        this.category = review.getPost().getCategory();
        this.email = review.getMember().getEmail();
        this.star = review.getStar();
        this.review = review.getReview();
        this.image = review.getImage();
        this.nickname = review.getMember().getNickname();
        this.createdAt = review.getCreatedAt();
        this.modifiedAt = review.getModifiedAt();
        this.postId = review.getPost().getId();
        this.memberImage = review.getMember().getImage();
    }

    public static ReviewResponseDto from(Review review) {
        return ReviewResponseDto.builder()
                .review(review)
                .build();
    }


}
