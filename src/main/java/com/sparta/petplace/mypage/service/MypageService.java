package com.sparta.petplace.mypage.service;

import com.sparta.petplace.S3Service;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.ResponseUtils;
import com.sparta.petplace.common.SuccessResponse;
import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.Error;
import com.sparta.petplace.like.entity.Likes;
import com.sparta.petplace.like.repository.LikesRepository;
import com.sparta.petplace.member.dto.MemberResponseDto;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.member.repository.MemberRepository;
import com.sparta.petplace.mypage.dto.MypageModifyRequestDto;
import com.sparta.petplace.post.ResponseDto.PostResponseDto;
import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.post.repository.PostRepository;
import com.sparta.petplace.review.dto.ReviewResponseDto;
import com.sparta.petplace.review.entity.Review;
import com.sparta.petplace.review.repository.ReviewRepository;
import com.sparta.petplace.review.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MypageService {
    private final LikesRepository likesRepository;
    private final PostRepository postRepository;
    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final S3Uploader s3Uploader;


    // 사업자가 본인이 작성한 게시글 조회 [사업자]
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getView(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Post> found = postRepository.findAllByEmail(member.getEmail());
        if (found.isEmpty()) {
            return null;
//            throw new CustomException(Error.NOT_FOUND_POST);
        }
        List<PostResponseDto> responseDtos = new ArrayList<>();
        for (Post post : found) {
            int count = 0;
            if (!post.getReviews().isEmpty()) {
                count = post.getReviews().size();
            }
            Likes likes = likesRepository.findByPostIdAndMemberId(post.getId(), member.getId());
            boolean isLike = likes != null;
            responseDtos.add(PostResponseDto.builder()
                    .post(post)
                    .isLike(isLike)
                    .reviewCount(count)
                    .build());
        }
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responseDtos.size());
        return new PageImpl<>(responseDtos.subList(start, end), pageable, responseDtos.size());
    }


    // 찜한 게시글 보여주기 [사업자,일반유저 공통]
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getSave(Member member, int page, int size) {
        log.info("getSave 메서드");
        Pageable pageable = PageRequest.of(page - 1, size);

        List<Likes> mypageList = likesRepository.findByMemberId(member.getId());
        List<PostResponseDto> responseDtoList = new ArrayList<>();
        for (Likes mypage : mypageList) {
            Integer reviewStar = 0;
            int count = 0;
            int starAvr = 0;
            if (!mypage.getPost().getReviews().isEmpty()) {
                for (Review r : mypage.getPost().getReviews()) {
                    reviewStar += r.getStar();
                    count += 1;
                }
                if (count != 0) {
                    starAvr = Math.round(reviewStar / count);
                }
            }
            responseDtoList.add(PostResponseDto.of(mypage.getPost(), starAvr));
        }
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responseDtoList.size());
        return new PageImpl<>(responseDtoList.subList(start, end), pageable, responseDtoList.size());
    }


    // 유저 프로필 정보 수정 [사업자,일반유저 공통]
    @Transactional
    public ApiResponseDto<SuccessResponse> modify(MypageModifyRequestDto requestDto, Member member) {
        Optional<Member> findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember.isEmpty()) {
            throw new CustomException(Error.NOT_EXIST_USER);
        }
        if (requestDto.getImage() == null || requestDto.getImage().isEmpty()) {
            findMember.get().update(requestDto.getNickname());
        } else {
            if (findMember.get().getImage() != null) {
                s3Service.deleteFile(findMember.get().getImage());
            }
            String image = null;
            try {
                image = s3Uploader.upload(requestDto.getImage(), requestDto.getImage().getOriginalFilename());
            } catch (IOException e) {
                throw new CustomException(Error.FAIL_S3_SAVE);
            }
            findMember.get().update(requestDto.getNickname(), image);
        }
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "수정완료"));
    }


    // 유저 프로필 정보 조회 [사업자,일반유저 공통]
    public ApiResponseDto<MemberResponseDto> getMember(Member member) {
        Optional<Member> found = memberRepository.findByEmail(member.getEmail());
        if (found.isEmpty()) {
            throw new CustomException(Error.NOT_EXIST_USER);
        }
        MemberResponseDto responseDto = MemberResponseDto.from(member);
        return ResponseUtils.ok(responseDto);
    }


    // 사용자 리뷰 조회 [일반유저]
    public Page<ReviewResponseDto> getReview(Member member, int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size);

        List<Review> review = reviewRepository.findAllByMemberId(member.getId());

        List<ReviewResponseDto> responseDtoList = new ArrayList<>();
        if (review.isEmpty()) {
            return null;
        }
        for (Review r : review) {
            responseDtoList.add(ReviewResponseDto.from(r));
        }
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responseDtoList.size());
        return new PageImpl<>(responseDtoList.subList(start, end), pageable, responseDtoList.size());
    }
}
