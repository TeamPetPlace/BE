package com.sparta.petplace.post.service;

import com.sparta.petplace.member.entity.LoginType;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.member.repository.MemberRepository;
import com.sparta.petplace.post.RequestDto.PostRequestDto;
import com.sparta.petplace.post.ResponseDto.PostResponseDto;
import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.post.entity.Sort;
import com.sparta.petplace.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PostService postService;

    private Member member;
    private Post post1;
    private Post post2;

    @BeforeEach
    void setUp() {
        String rawPassword = "di12341234!";
        member = Member.builder()
                .email("kjw8922@naver.com")
                .password(passwordEncoder.encode(rawPassword))
                .nickname("testNickname")
                .business("287-25-01555")
                .image(null)
                .loginType(LoginType.BUSINESS)
                .build();
        memberRepository.save(member);

        String title = "게시글1";
        String category = "카테고리1";
        String ceo = "ceo";
        String contens = "컨텐츠";
        String lat = "1111.1111";
        String lng = "123.123";
        String address = "address";
        String telNum = "01000000000";
        String closedDay = "금요일";
        PostRequestDto postRequestDto = new PostRequestDto(title, category, ceo, contens, lat, lng, address, telNum, closedDay);
        post1 = new Post(postRequestDto, member, null, null);
        postRepository.save(post1);

        String title2 = "게시글2";
        String category2 = "카테고리1";

        PostRequestDto postRequestDto2 = new PostRequestDto(title2, category2, ceo, contens, lat, lng, address, telNum, closedDay);
        post2 = new Post(postRequestDto2, member, null, null);
        postRepository.save(post2);
    }

    @Test
    @DisplayName("카테고리별 전체 게시글 불러오기 테스트")
    @Transactional
    void getPosts() {
        // given
        String category = "카테고리1";
        Sort sort = Sort.DISTANCE;
        String lat = "111.1111";
        String lng = "1123.11113";
        int page = 0;
        int size = 2;
        // when
        Page<PostResponseDto> result = postService.getPosts(category, sort, lat, lng, page, size, member);

        // then
        assertEquals(post1.getTitle(), result.get().collect(Collectors.toList()).get(0).getTitle());
        assertEquals(post2.getTitle(), result.get().collect(Collectors.toList()).get(1).getTitle());
    }

    @Test
    void getMain() {
    }

    @Test
    void createPost() {
    }

    @Test
    void postCheck() {
    }

    @Test
    void getPostId() {
    }

    @Test
    void updatePost() {
    }

    @Test
    void deletePost() {
    }

    @Test
    void searchPost() {
    }

    @Test
    void getPostInfo() {
    }
}