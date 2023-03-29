package com.sparta.petplace.review.service;

import com.sparta.petplace.S3Service;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.ResponseUtils;
import com.sparta.petplace.common.SuccessResponse;
import com.sparta.petplace.common.sse.service.NotificationService;
import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.Error;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.post.repository.PostRepository;
import com.sparta.petplace.review.dto.ReviewRequestDto;
import com.sparta.petplace.review.dto.ReviewResponseDto;
import com.sparta.petplace.review.entity.Review;
import com.sparta.petplace.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;
    @Transactional
    public ApiResponseDto<ReviewResponseDto> createReview(Long post_id, ReviewRequestDto requestDto, Member member) {
        Post post = postRepository.findById(post_id).orElseThrow(
                () -> new CustomException(Error.NOT_FOUND_POST)
        );
        // 1. 사용자와 게시글에 해당하는 가장 최근의 리뷰를 조회합니다.
        Review latestReview = reviewRepository.findTopByMemberAndPostOrderByCreatedAtDesc(member, post);
        // 2. 조회한 리뷰의 createdAt을 확인하여 현재 시간과 비교해 1주일이 경과했는지 확인합니다.
        if (latestReview != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneWeekAgo = now.minusWeeks(1);
            if (latestReview.getCreatedAt().isAfter(oneWeekAgo)) {
                // 1주일이 경과하지 않은 경우 예외를 던집니다.
                throw new CustomException(Error.CANNOT_CREATE_REVIEW_WITHIN_ONE_WEEK);
            }
        }
        // 3. 1주일이 경과했을 경우에만 새로운 리뷰를 작성할 수 있게 합니다.
        if (requestDto.getImage() == null || requestDto.getImage().isEmpty()) {
            Review review = reviewRepository.save(new Review(requestDto, null, post, member));
            return ResponseUtils.ok(ReviewResponseDto.from(review));
        }

        // 4. 댓글 생성시 작성자에게 알림 전송
        String content = post.getMember().getNickname()+"님!" + post.getTitle() + " 게시글 리뷰 알림이 도착했어요!";
        notificationService.send(post.getMember(), content, "testUrl");


        String image = s3Service.uploadMypage(requestDto.getImage());
        Review review = reviewRepository.save(new Review(requestDto, image, post, member));
        return ResponseUtils.ok(ReviewResponseDto.from(review));
    }


    // 후기 수정
    @Transactional
    public ApiResponseDto<ReviewResponseDto> updateReview(Long review_id, ReviewRequestDto requestDto, Member member) {
        Review review = reviewRepository.findById(review_id).orElseThrow(
                () -> new IllegalArgumentException("해당 후기가 존재하지 않습니다.")
        );
        Optional<Review> exist = reviewRepository.findByIdAndMember(review_id, member);
        if (exist.isEmpty()) {
            throw new CustomException(Error.NOT_MY_CONTENT);
        }
        if (review.getImage() != null) {
            s3Service.deleteFile(review.getImage());
        }
        if (requestDto.getImage() == null || requestDto.getImage().isEmpty()){
            review.update(requestDto, null, member);
            return ResponseUtils.ok(ReviewResponseDto.from(review));
        }
            String image = s3Service.uploadMypage(requestDto.getImage());
            review.update(requestDto, image, member);
            return ResponseUtils.ok(ReviewResponseDto.from(review));
        }


    // 후기 삭제
    public ApiResponseDto<SuccessResponse> deleteReview(Long review_id, Member member) {
        Review review = reviewRepository.findById(review_id).orElseThrow(
                () -> new IllegalArgumentException("해당하는 후기가 존재하지 않습니다.")
        );
        Optional<Review> exist = reviewRepository.findByIdAndMember(review_id, member);
        if (exist.isEmpty()) {
            throw new CustomException(Error.NOT_MY_CONTENT);
        }
        if (review.getImage() != null) {
            s3Service.deleteFile(review.getImage());
        }
        reviewRepository.deleteById(review_id);
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "후기 삭제 완료"));
    }



}
