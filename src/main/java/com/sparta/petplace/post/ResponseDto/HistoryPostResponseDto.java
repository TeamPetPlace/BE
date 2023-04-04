package com.sparta.petplace.post.ResponseDto;

import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.review.dto.ReviewResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class HistoryPostResponseDto {

    private Long id;
    private String title;
    private String reSizeImage;

    @Builder
    public HistoryPostResponseDto(Post post){
        this.id = post.getId();
        this.reSizeImage = post.getResizeImage();
        this.title = post.getTitle();
    }

    public static HistoryPostResponseDto of(Post post){
        return HistoryPostResponseDto.builder()
                .post(post)
                .build();
    }
}

