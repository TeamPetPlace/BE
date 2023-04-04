package com.sparta.petplace.review.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ReviewRequestDto {
    private Integer star;
    private String review;
    private MultipartFile image;
}
