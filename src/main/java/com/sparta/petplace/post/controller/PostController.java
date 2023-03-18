package com.sparta.petplace.post.controller;


import com.sparta.petplace.auth.security.UserDetailsImpl;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.SuccessResponse;
import com.sparta.petplace.post.RequestDto.PostRequestDto;
import com.sparta.petplace.post.ResponseDto.PostResponseDto;
import com.sparta.petplace.post.entity.Sort;
import com.sparta.petplace.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {

   private final PostService postService;

   // TODO: 2023/03/17 올리기전에 프론트에 키워드 -> 카테고리 로 바꾼거 말해주기 , sort 추가한거도 말해주기
   //게시글 전체 조회
   @GetMapping("/category")
   public List<PostResponseDto> getPosts(@RequestParam(value = "category") String category,
                                         @RequestParam(value = "sort") Sort sort,
                                         @RequestParam(value = "lat") String lat,
                                         @RequestParam(value = "lng") String lng){
      return postService.getPosts(category, sort, lat, lng);
   }


   //게시글 상세 조회
   @GetMapping("/{post_id}")
   private PostResponseDto getPostId(@PathVariable Long post_id ,@AuthenticationPrincipal UserDetailsImpl userDetails){
      return postService.getPostId(post_id,userDetails.getMember());
   }


   //게시글 이름 중복 확인
   @GetMapping("/check_duplicate")
   public ApiResponseDto<SuccessResponse> postCheck(@RequestParam(value = "title") String title){
      return postService.postCheck(title);
   }


   //게시글 작성
   @PostMapping("/write")
   public ApiResponseDto<PostResponseDto> createPost(@ModelAttribute() PostRequestDto requestDto,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails){
      return postService.createPost(requestDto,userDetails.getMember());
   }


   //게시글 수정
   @PatchMapping("/{post_id}")
   public ApiResponseDto<?> updePost(@PathVariable Long post_id , PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
      return postService.updatePost(post_id,requestDto,userDetails.getMember());
   }


   //게시글 삭제
   @DeleteMapping("/{post_id}")
   public ApiResponseDto<SuccessResponse> deletePost(@PathVariable Long post_id, @AuthenticationPrincipal UserDetailsImpl userDetails){
      return postService.deletePost(post_id, userDetails.getMember());
   }

}
