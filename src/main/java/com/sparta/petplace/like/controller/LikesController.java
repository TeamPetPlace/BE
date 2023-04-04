package com.sparta.petplace.like.controller;

import com.sparta.petplace.auth.security.UserDetailsImpl;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.ResponseUtils;
import com.sparta.petplace.common.SuccessResponse;
import com.sparta.petplace.like.dto.LikesResponseDto;
import com.sparta.petplace.like.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("{post_id}")
public class LikesController {
    private final LikesService likesService;

    //게시글 찜하기
    @PostMapping("/like")
    public ApiResponseDto<LikesResponseDto> like(@PathVariable Long post_id,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails){
    return likesService.like(post_id,userDetails);
    }

    //게시글 찜하기 취소
    @DeleteMapping("/cancel")
    public ApiResponseDto<LikesResponseDto> cancel(@PathVariable Long post_id,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails){
        return likesService.cancel(post_id,userDetails);
    }
}



