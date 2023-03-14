package com.sparta.petplace.post.service;


import com.sparta.petplace.S3Service;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.ErrorResponse;
import com.sparta.petplace.common.ResponseUtils;
import com.sparta.petplace.common.SuccessResponse;
import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.enumclass.Error;
import com.sparta.petplace.like.entity.Likes;
import com.sparta.petplace.like.repository.LikesRepository;
import com.sparta.petplace.member.entity.LoginType;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.member.repository.MemberRepository;
import com.sparta.petplace.mypage.repository.MypageRepository;
import com.sparta.petplace.post.RequestDto.PostRequestDto;
import com.sparta.petplace.post.ResponseDto.PostResponseDto;
import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.post.entity.PostImage;
import com.sparta.petplace.post.repository.PostImageRepository;
import com.sparta.petplace.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final MypageRepository mypageRepository;
    private final LikesRepository likesRepository;


    @Transactional
    public List<PostResponseDto> getPosts(String keyword) {
        List<Post> posts = postRepository.findAByCategory(keyword);
        List<PostResponseDto> postResponseDtos = new ArrayList<>();

        for (Post p : posts) {
//            p.getReviews().sort(Comparator.comparing(Review::gerCreatedAt).reversed());
//            List<ReviewResponseDto> reviewResponseDtos = new ArrayList<>();
//            for(Review r : p.getReviews()){
//                reviewResponseDtos.add(new ReviewResponseDtoa(r));
//            }
            postResponseDtos.add(PostResponseDto.builder()
                    .post(p)
                    .build());
        }
        return postResponseDtos;
    }

    @Transactional
    public ApiResponseDto<PostResponseDto> createPost(PostRequestDto requestDto, Member member) {
        Member member1 = memberRepository.findByLoginType(member.getLoginType());
        if (!member1.getLoginType().equals(LoginType.BUSINESS)) {
            throw new CustomException(Error.NO_AUTHORITY);
        }
        if (requestDto.getImage().isEmpty()) {
            throw new CustomException(Error.WRONG_INPUT_CONTEN);
        }
        if (requestDto.getImage().size() > 4) {
            throw new RuntimeException("사진은 4개이상 저장할 수 없습니다.");
        }
        Post posts = Post.of(requestDto, member);
        postRepository.save(posts);
        List<String> imgList = new ArrayList<>();
        List<String> img_url = s3Service.upload(requestDto.getImage());
        for (String image : img_url) {
            PostImage img = new PostImage(posts, image);
            postImageRepository.save(img);
            imgList.add(image);
        }
        return ResponseUtils.ok(PostResponseDto.from(posts, imgList));
    }

    @Transactional
    public PostResponseDto getPostId(Long post_id, Member member) {
        Post posts = postRepository.findById(post_id).orElseThrow(
                () -> new CustomException(Error.NOT_FOUND_POST)
        );
        List<String> images = new ArrayList<>();
        for (PostImage postImage : posts.getImage()) {
            images.add(postImage.getImage());
        }

//        posts.getReviews().sort(Comparator.comparing(Review::gerCreatedAt).reversed());
//        List<ReviewResponseDto> reviewResponseDtos = new ArrayList<>();
//        for (Review r : p.getReviews()) {
//            reviewResponseDtos.add(new ReviewResponseDtoa(r));
//        }
        Likes likes = likesRepository.findByPostIdAndMember(post_id, member);
        if (likes == null) {
            return PostResponseDto.of(posts, images, false);
        } else {
            return PostResponseDto.of(posts, images, true);
        }
    }

    @Transactional
    public ApiResponseDto<?> updatePost(Long post_id, PostRequestDto requestDto, Member member) {
        Optional<Post> postOptional = postRepository.findById(post_id);
        //게시글 확인
        if (postOptional.isEmpty()) {
            throw new CustomException(Error.NOT_FOUND_POST);
        }
        Post post = postOptional.get();
        if (post.getMember().getEmail().equals(member.getEmail())) {
            //기존 S3에 저장된 파일을 제거후 다시 저장
            for (PostImage postImage : post.getImage()) {
                s3Service.deleteFile(postImage.getImage());
                postImageRepository.delete(postImage);
            }
            //다시 선택한 데이터를 저장, save
            List<String> imgList = new ArrayList<>();
            List<String> img_url = s3Service.upload(requestDto.getImage());
            for (String image : img_url) {
                PostImage img = new PostImage(post, image);
                postImageRepository.save(img);
                imgList.add(image);
            }
            //List<String> -> List<PostImage>로 변환
            List<PostImage> postImages = new ArrayList<>();
            for (String image : imgList) {
                PostImage postImage = new PostImage(post, image);
                postImages.add(postImage);
            }
            // Post 객체 업데이트
            post.update(requestDto, postImages);
            return ResponseUtils.ok(PostResponseDto.of(post));
        } else {
            return ResponseUtils.ok(ErrorResponse.of(HttpStatus.BAD_REQUEST.toString(), "작성자만 게시물을 수정할 수 있습니다."));
        }
    }

    @Transactional
    public ApiResponseDto<SuccessResponse> deletePost(Long post_id, Member member) {
        Optional<Post> postOptional = postRepository.findById(post_id);
        if (postOptional.isEmpty()) {
            throw new CustomException(Error.NOT_FOUND_POST);
        }
        Post post = postOptional.get();
        if (!post.getMember().getEmail().equals(member.getEmail())) {
            throw new CustomException(Error.NO_AUTHORITY);
        }
        //기존 S3에 저장된 파일을 제거후 다시 저장
        for (PostImage postImage : post.getImage()) {
            s3Service.deleteFile(postImage.getImage());
            postImageRepository.delete(postImage);
        }
        likesRepository.deleteByPostId(post_id);
        mypageRepository.deleteByPostId(post_id);
        postRepository.deleteById(post_id);
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, " 게시글 삭제 성공"));
    }

}
