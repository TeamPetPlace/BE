package com.sparta.petplace.post.ResponseDto;

import com.sparta.petplace.post.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HistoryPostResponseDto {

    private Long id;
    private String title;
    private String reSizeImage;
    private String category;

    @Builder
    public HistoryPostResponseDto(Post post){
        this.id = post.getId();
        this.category = post.getCategory();
        this.reSizeImage = post.getResizeImage();
        this.title = post.getTitle();
    }

    public static HistoryPostResponseDto of(Post post){
        return HistoryPostResponseDto.builder()
                .post(post)
                .build();
    }
}

