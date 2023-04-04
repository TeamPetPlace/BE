package com.sparta.petplace.like.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LikesResponseDto {
    private boolean likes;
    private String msg;
    private HttpStatus status;

    @Builder
    public LikesResponseDto(boolean likes,String msg,HttpStatus status){
        this.likes = likes;
        this.msg = msg;
        this.status = status;
    }
    public static LikesResponseDto of(boolean likes, String msg,HttpStatus status){
        return LikesResponseDto.builder()
                .likes(likes)
                .msg(msg)
                .status(status)
                .build();
    }
}
