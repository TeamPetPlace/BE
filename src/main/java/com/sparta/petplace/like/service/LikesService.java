package com.sparta.petplace.like.service;

import com.sparta.petplace.auth.security.UserDetailsImpl;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.ResponseUtils;
import com.sparta.petplace.like.dto.LikesResponseDto;
import com.sparta.petplace.like.entity.Likes;
import com.sparta.petplace.like.repository.LikesRepository;
import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesRepository likesRepository;
    private final PostRepository postRepository;

    //게시글 찜하기
    @Transactional
    public ApiResponseDto<LikesResponseDto> like(Long postId, UserDetailsImpl userDetails) {
        Post post = getPost(postId);
        Optional<Likes> likes = likesRepository.findByPostAndMember(post,userDetails.getMember());
        if(likes.isEmpty()){
            Likes found = Likes.of(userDetails.getMember(),post);
            likesRepository.save(found);
        }
        return ResponseUtils.ok(LikesResponseDto.of(true, "찜",HttpStatus.OK));
    }

    //게시글 찜히기 취소
    @Transactional
    public ApiResponseDto<LikesResponseDto> cancel(Long postId, UserDetailsImpl userDetails) {
        Post post = getPost(postId);
        Optional<Likes> likes = likesRepository.findByPostAndMember(post,userDetails.getMember());
        if(!likes.isEmpty()){
            likesRepository.delete(likes.get());
        }
        return ResponseUtils.ok(LikesResponseDto.of(false, "취소", HttpStatus.OK));
    }

    // ======== 메서드 ========
    //예외
    private Post getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("게시물을 찾을 수 없습니다.")
        );
        return post;
    }


}
