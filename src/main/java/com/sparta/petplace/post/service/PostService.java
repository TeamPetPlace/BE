package com.sparta.petplace.post.service;


import com.sparta.petplace.S3Service;
import com.sparta.petplace.common.*;
import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.Error;
import com.sparta.petplace.like.entity.Likes;
import com.sparta.petplace.like.repository.LikesRepository;
import com.sparta.petplace.member.entity.LoginType;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.member.entity.MemberHistory;
import com.sparta.petplace.member.repository.MemberHistoryRepository;
import com.sparta.petplace.member.repository.MemberRepository;
import com.sparta.petplace.post.RequestDto.PostRequestDto;
import com.sparta.petplace.post.ResponseDto.PostResponseDto;
import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.post.entity.PostImage;
import com.sparta.petplace.post.entity.Sort;
import com.sparta.petplace.post.repository.PostImageRepository;
import com.sparta.petplace.post.repository.PostRepository;
import com.sparta.petplace.review.dto.ReviewResponseDto;
import com.sparta.petplace.review.entity.Review;
import com.sparta.petplace.review.repository.ReviewRepository;
import com.sparta.petplace.review.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final MemberHistoryRepository memberHistoryRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final LikesRepository likesRepository;
    private final ReviewRepository reviewRepository;
    private final S3Uploader s3Uploader;



    // 게시글 카테고리별 (전체)조회
    @LogExecutionTime
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPosts(String category, Sort sort, String lat, String lng, int page, int size, Member member) {

        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, size);
        Double usrtLat = Double.parseDouble(lat);
        Double usrtLng = Double.parseDouble(lng);
        List<Post> posts = postRepository.find(category, pageable, usrtLat, usrtLng, sort);


        buildResponseDtos(member, postResponseDtos, posts, usrtLat, usrtLng, sort);
        long totalCount = postRepository.countByCategory(category);

        return new PageImpl<>(postResponseDtos, pageable, totalCount);
    }

    // 메인 페이지 조회
    @LogExecutionTime
    @Transactional(readOnly = true)
    public List<PostResponseDto> getMain(String category, String lat, String lng, Member member) {

        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 3);
        List<Post> posts = postRepository.findByCategory(category, pageable);
        Double usrtLat = Double.parseDouble(lat);
        Double usrtLng = Double.parseDouble(lng);

        // PostResponseDto 생성
        buildResponseDtos(member, postResponseDtos, posts, usrtLat, usrtLng,Sort.DISTANCE);

        return postResponseDtos.subList(0, Math.min(postResponseDtos.size(), 3));
    }


    // 게시글 작성
    @Transactional
    public ApiResponseDto<PostResponseDto> createPost(PostRequestDto requestDto, Member member) {
        Optional<Member> member1 = memberRepository.findByEmail(member.getEmail());
        if (member1.isEmpty()) {
            throw new CustomException(Error.NOT_EXIST_USER);
        }
        if (!member1.get().getLoginType().equals(LoginType.BUSINESS)) {
            throw new CustomException(Error.NO_AUTHORITY);
        }
        if (requestDto.getImage() == null || requestDto.getImage().isEmpty()) {
            throw new CustomException(Error.WRONG_INPUT_CONTEN);
        }
        if (requestDto.getImage().size() > 4) {
            throw new CustomException(Error.MAX_INPUT_IMAGE);
        }
        Optional<Post> postName = postRepository.findByTitle(requestDto.getTitle());
        if (postName.isPresent()) {
            throw new CustomException(Error.DUPLICATED_BUSINESS);
        }

        Post posts = Post.of(requestDto, member);
        List<String> imgList = new ArrayList<>();
        List<String> img_url = s3Service.upload(requestDto.getImage());
        for (String image : img_url) {
            PostImage img = new PostImage(posts, image);
            postImageRepository.save(img);
            imgList.add(image);
        }
        String d;
        // s3 이미지 업로드 try - catch
        try {
            d = s3Uploader.upload(resizeImage(requestDto.getImage().get(0)), requestDto.getImage().get(0).getOriginalFilename());
            posts.setResizeImage(d);
            log.info("createPost 메서드 / try 진입 = "+d);
        } catch (IOException e) {
            throw new CustomException(Error.FAIL_S3_SAVE);
        }

        postRepository.save(posts);
        return ResponseUtils.ok(PostResponseDto.from(posts, imgList));
    }


    // 게시글 이름 중복 확인
    @Transactional(readOnly = true)
    public ApiResponseDto<SuccessResponse> postCheck(String title) {
        Optional<Post> findPost = postRepository.findByTitle(title);
        if (findPost.isPresent()) {
            throw new CustomException(Error.DUPLICATED_BUSINESS);
        }
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "등록 가능한 사업자입니다."));
    }


    // 게시글 상세 조회
    @LogExecutionTime
    @Transactional
    public ApiResponseDto<PostResponseDto> getPostId(Long post_id, Member member) {
        Post posts = postRepository.findById(post_id).orElseThrow(
                () -> new CustomException(Error.NOT_FOUND_POST)
        );
        List<String> images = new ArrayList<>();
        for (PostImage postImage : posts.getImage()) {
            images.add(postImage.getImage());
        }

        List<Review> reviews = posts.getReviews();
        int reviewStar = reviews.stream()
                .mapToInt(Review::getStar)
                .sum();
        int count = posts.getReviews().size();
        int starAvr = 0;
        if (count != 0) {
            starAvr =  (int)((reviewStar/(float)count)+0.5);
        }

        Likes likes = likesRepository.findByPostIdAndMemberId(post_id, member.getId());

        PostResponseDto postResponseDto = PostResponseDto.of(posts, images, likes != null, count, starAvr);

        List<MemberHistory> memberHistories = memberHistoryRepository.findTop3ByMemberOrderByCreatedAtDesc(member);

        // findFirst(): filter를 통과한 첫 번째 요소를 찾음. findFirst는 Optional<MemberHistory> 타입을 반환하며, 조건에 맞는 요소가 없는 경우 Optional.empty()를 반환
        Optional<MemberHistory> existingHistory = memberHistories.stream()
                .filter(history -> history.getPost().getId().equals(post_id))
                .findFirst();

        // 중복등록 될 경우 삭제 후 업데이트
        if (existingHistory.isPresent()) {
            MemberHistory historyToDelete = existingHistory.get();
            memberHistoryRepository.delete(historyToDelete);

            MemberHistory newHistory = MemberHistory.of(member, posts, new Date());
            memberHistoryRepository.save(newHistory);

            // 사이즈 3개보다 크다면 순서대로 가장 오래된 게시글 삭제 후 새로운 게시글 등록
        } else {
            if (memberHistories.size() >= 3) {
                MemberHistory oldestHistory = memberHistories.get(memberHistories.size() - 1);
                memberHistoryRepository.delete(oldestHistory);
            }
            memberHistoryRepository.save(MemberHistory.of(member, posts, new Date()));
        }
        return ResponseUtils.ok(postResponseDto);
    }


    // 게시글 수정
    @Transactional
    public ApiResponseDto<?> updatePost(Long post_id, PostRequestDto requestDto, Member member) {
        Optional<Post> postOptional = postRepository.findById(post_id);

        if (postOptional.isEmpty()) {
            throw new CustomException(Error.NOT_FOUND_POST);
        }
        Post post = postOptional.get();
        if (post.getMember().getEmail().equals(member.getEmail())) {
            //  기존이미지 삭제
            for (PostImage postImage : post.getImage()) {
                s3Service.deleteFile(postImage.getImage());
                postImageRepository.delete(postImage);
            }
            s3Service.deleteFile(post.getResizeImage());
            List<PostImage> postImages = new ArrayList<>();
            List<String> img_url = s3Service.upload(requestDto.getImage());
            //  이미지 Repository 를 통해 DB에 저장
            for (String image : img_url) {
                PostImage img = new PostImage(post, image);
                postImageRepository.save(img);
                postImages.add(img);
            }

            String d;
            // s3 이미지 업로드 try - catch
            try {
                d = s3Uploader.upload(resizeImage(requestDto.getImage().get(0)), requestDto.getImage().get(0).getOriginalFilename());
                post.update(requestDto, postImages, post.getStar());
                post.setResizeImage(d);
            } catch (IOException e) {
                throw new CustomException(Error.FAIL_S3_SAVE);
            }

            return ResponseUtils.ok(PostResponseDto.from(post, img_url));
        } else {
            return ResponseUtils.ok(ErrorResponse.of(HttpStatus.BAD_REQUEST.toString(), "작성자만 게시물을 수정할 수 있습니다."));
        }
    }


    //게시글 삭제
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
        for (PostImage postImage : post.getImage()) {
            s3Service.deleteFile(postImage.getImage());
            postImageRepository.delete(postImage);
        }
        memberHistoryRepository.deleteByPostId(post_id);
        reviewRepository.deleteByPostId(post_id);
        likesRepository.deleteByPostId(post_id);
        postRepository.deleteById(post_id);
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, " 게시글 삭제 성공"));
    }


    // 게시글 검색 조회
    @LogExecutionTime
    @Transactional(readOnly = true)
    public Page<PostResponseDto> searchPost(String category, String keyword, Sort sort, String lat, String lng, int page, int size, Member member) {
        Pageable pageable = PageRequest.of(page, size);
        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        // QueryDSL 사용
        Double usrtLat = Double.parseDouble(lat);
        Double usrtLng = Double.parseDouble(lng);
        List<Post> posts = postRepository.search(category, keyword, usrtLat, usrtLng, pageable, sort);

        if (posts.isEmpty()) {
            throw new CustomException(Error.NOT_FOUND_POST);
        }

        buildResponseDtos(member, postResponseDtos, posts, usrtLat, usrtLng,sort);
        long totalCount = postRepository.countByCategoryAndKeyword(category, keyword);

        return new PageImpl<>(postResponseDtos, pageable, totalCount);
    }



    //리뷰 페이지네이션
    public Page<ReviewResponseDto> getPostInfo(Long post_id, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Post posts = postRepository.findById(post_id).orElseThrow(
                () -> new CustomException(Error.NOT_FOUND_POST)
        );
        // 최신순 정렬
        posts.getReviews().sort(Comparator.comparing(Review::getCreatedAt).reversed());
        List<ReviewResponseDto> reviewResponseDtos = new ArrayList<>();
        for (Review r : posts.getReviews()) {
            reviewResponseDtos.add(ReviewResponseDto.from(r));
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), reviewResponseDtos.size());
        return new PageImpl<>(reviewResponseDtos.subList(start, end), pageable, reviewResponseDtos.size());

    }


    // ==================================== Method Extract ====================================

    //거리구하기
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // 지구 반지름
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return distance * 1000.0;
    }


    private void buildResponseDtos(Member member, List<PostResponseDto> postResponseDtos, List<Post> posts, Double usrtLat, Double usrtLng, Sort sort) {
        List<PostResponseDto> dtoList = posts.stream()
                .map(p -> {
                    Double postLat = Double.parseDouble(p.getLat());
                    Double postLng = Double.parseDouble(p.getLng());
                    double distance = distance(usrtLat, usrtLng, postLat, postLng);
                    int starAvr = 0;
                    List<Review> reviews = p.getReviews();
                    int[] countAndStarSum = {0, 0};
                    synchronized(reviews) { // 동기화 처리
                        reviews.forEach(r -> {
                            countAndStarSum[0]++; // count 증가
                            countAndStarSum[1] += r.getStar(); // star 합계
                        });
                    }
                    int count = countAndStarSum[0];
                    int reviewStar = countAndStarSum[1];
                    if (count != 0) {
                        starAvr =  (int)((reviewStar/(float)count)+0.5);
                    }
                    Likes likes = likesRepository.findByPostIdAndMemberId(p.getId(), member.getId());
                    boolean isLike = likes != null;
                    return PostResponseDto.builder()
                            .post(p)
                            .star(starAvr)
                            .distance(distance)
                            .reviewCount(count)
                            .isLike(isLike)
                            .build();
                })
                .toList();
        postResponseDtos.addAll(dtoList);
    }



    // 이미지 리사이징
    private File resizeImage(MultipartFile file) throws IOException {
        log.info(file.getContentType());

        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        int originWidth = originalImage.getWidth();
        int originHeight = originalImage.getHeight();

        int newWidth = 400;
        int newHeight = 0;
        if (originWidth > newWidth) {
            newHeight = (originHeight * newWidth) / originWidth;
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        graphics2D.dispose();

        File outputfile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        log.info(outputfile.getAbsolutePath());
        try {
            if (outputfile.createNewFile()) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                String type = Objects.requireNonNull(file.getContentType()).substring(file.getContentType().indexOf("/") + 1);
                ImageIO.write(resizedImage, type, bos);

                InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());

                Files.copy(inputStream, outputfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                return outputfile;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return outputfile;

    }
}
