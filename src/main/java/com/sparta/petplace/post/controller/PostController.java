package com.sparta.petplace.post.controller;


import com.sparta.petplace.auth.security.UserDetailsImpl;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.SuccessResponse;
import com.sparta.petplace.post.RequestDto.PostRequestDto;
import com.sparta.petplace.post.ResponseDto.PostResponseDto;
import com.sparta.petplace.post.entity.Sort;
import com.sparta.petplace.post.service.PostService;
import com.sparta.petplace.review.dto.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {

   private final PostService postService;


   // 게시글 전체 조회
   @GetMapping("/category")
   public Page<PostResponseDto> getPosts(@RequestParam(value = "category") String category,
                                         @RequestParam(value = "sort") Sort sort,
                                         @RequestParam(value = "lat") String lat,
                                         @RequestParam(value = "lng") String lng,
                                         @RequestParam(value = "page") int page,
                                         @RequestParam(value = "size") int size,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
      return postService.getPosts(category, sort, lat, lng, page, size, userDetails.getMember());
   }


   // 게시글 상세 조회
   @GetMapping("/{post_id}")
   private ApiResponseDto<PostResponseDto> getPostId(@PathVariable Long post_id ,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails){
      return postService.getPostId(post_id,userDetails.getMember());
   }


   // 리뷰 페이지네이션
   @GetMapping("/{post_id}/review")
   private Page<ReviewResponseDto> getPostInfo(@PathVariable Long post_id ,
                                         @RequestParam(value = "page") int page,
                                         @RequestParam(value = "size") int size){
      return postService.getPostInfo(post_id, page, size);
   }


   // 메인페이지
   @GetMapping("/topPosts")
   public List<PostResponseDto> getMains(@RequestParam(value = "category") String category,
                                         @RequestParam(value = "lat") String lat,
                                         @RequestParam(value = "lng") String lng,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
      return postService.getMain(category, lat, lng, userDetails.getMember());
   }


   // 게시글 이름 중복 확인
   @GetMapping("/check_duplicate")
   public ApiResponseDto<SuccessResponse> postCheck(@RequestParam(value = "title") String title){
      return postService.postCheck(title);
   }


   // 게시글 작성
   @PostMapping("/write")
   public ApiResponseDto<PostResponseDto> createPost(@ModelAttribute() PostRequestDto requestDto,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails){
      return postService.createPost(requestDto,userDetails.getMember());
   }


   // 게시글 수정
   @PatchMapping("/{post_id}")
   public ApiResponseDto<?> updePost(@PathVariable Long post_id , PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
      return postService.updatePost(post_id,requestDto,userDetails.getMember());
   }


   // 게시글 삭제
   @DeleteMapping("/{post_id}")
   public ApiResponseDto<SuccessResponse> deletePost(@PathVariable Long post_id, @AuthenticationPrincipal UserDetailsImpl userDetails){
      return postService.deletePost(post_id, userDetails.getMember());
   }


   // 게시글 검색
   @GetMapping("/category/search")
   public ApiResponseDto<List<PostResponseDto>> searchPost(@RequestParam(value = "category") String category,
                                                           @RequestParam(value = "keyword") String keyword,
                                                           @RequestParam(value = "sort") Sort sort,
                                                           @RequestParam(value = "lat") String lat,
                                                           @RequestParam(value = "lng") String lng,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails){
      return postService.searchPost(category,keyword, sort, lat, lng,userDetails.getMember());
   }
}
