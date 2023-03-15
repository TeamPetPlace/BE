package com.sparta.petplace.review.controller;

import com.sparta.petplace.auth.security.UserDetailsImpl;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.SuccessResponse;
import com.sparta.petplace.review.dto.ReviewRequestDto;
import com.sparta.petplace.review.dto.ReviewResponseDto;
import com.sparta.petplace.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class ReviewController {
    private final ReviewService reviewService;
    @PostMapping("/{post_id}/reviews")
    public ApiResponseDto<ReviewResponseDto> createReview(@PathVariable Long post_id,
                                                          @ModelAttribute ReviewRequestDto requestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reviewService.createReview(post_id, requestDto, userDetails.getMember());
    }

    @PutMapping("/reviews/{review_id}")
    public ApiResponseDto<ReviewResponseDto> updateReview(@PathVariable Long review_id,
                                                          @ModelAttribute ReviewRequestDto requestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reviewService.updateReview(review_id, requestDto, userDetails.getMember());
    }

    @DeleteMapping("/reviews/{review_id}")
    public ApiResponseDto<SuccessResponse> deleteReview(@PathVariable Long review_id,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reviewService.deleteReview(review_id, userDetails.getMember());
    }

}