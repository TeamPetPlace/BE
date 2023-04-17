package com.sparta.petplace.member.service;

import com.sparta.petplace.auth.jwt.JwtUtil;
import com.sparta.petplace.auth.jwt.RefreshToken;
import com.sparta.petplace.auth.jwt.RefreshTokenRepository;
import com.sparta.petplace.auth.jwt.TokenDto;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.SuccessResponse;
import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.Error;
import com.sparta.petplace.member.dto.BusinessSignupRequestDto;
import com.sparta.petplace.member.dto.LoginRequestDto;
import com.sparta.petplace.member.dto.LoginResponseDto;
import com.sparta.petplace.member.dto.SignupRequestDto;
import com.sparta.petplace.member.entity.LoginType;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.member.entity.MemberHistory;
import com.sparta.petplace.member.repository.MemberHistoryRepository;
import com.sparta.petplace.member.repository.MemberRepository;
import com.sparta.petplace.post.RequestDto.PostRequestDto;
import com.sparta.petplace.post.ResponseDto.HistoryPostResponseDto;
import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberHistoryRepository memberHistoryRepository;


    Member member;
    LoginRequestDto loginRequestDto;



    @BeforeEach
    public void setUp() {
        String rawPassword = "di12341234!";
        loginRequestDto = new LoginRequestDto("kjw8922@naver.com", rawPassword);
        member = Member.builder()
                .email("kjw8922@naver.com")
                .password(passwordEncoder.encode(rawPassword))
                .nickname("testNickname")
                .business("287-25-01555")
                .image(null)
                .loginType(LoginType.BUSINESS)
                .build();
    }


    @Test
    @DisplayName("일반 사용자 회원가입")
    @Transactional
    void 회원가입() {
        // given
        SignupRequestDto requestDto = new SignupRequestDto(member);

        // when
        ApiResponseDto<SuccessResponse> responseDto = memberService.signup(requestDto);

        // then
        assertEquals(HttpStatus.OK.value(), responseDto.getResponse().getStatus());
        assertEquals("회원가입 성공", responseDto.getResponse().getMessage());
    }

    @Test
    @DisplayName("사업자 회원가입 성공")
    @Transactional
    void 사업자회원가입() {

        // given
        BusinessSignupRequestDto signupRequestDto = new BusinessSignupRequestDto(member);
        // when
        ApiResponseDto<SuccessResponse> responseDto = memberService.businessSignup(signupRequestDto);
        // then
        assertEquals(HttpStatus.OK.value(), responseDto.getResponse().getStatus());
        assertEquals("회원가입 성공", responseDto.getResponse().getMessage());


    }

    @Test
    @DisplayName("로그인 테스트")
    @Transactional
    void 로그인테스트() {
        // given
        memberRepository.save(member);
        HttpServletResponse response = new MockHttpServletResponse();

        // when
        ApiResponseDto<LoginResponseDto> responseDto = memberService.login(loginRequestDto, response);
//        assertThrows(CustomException.class, () -> memberService.login(loginRequestDto, response));

        // then
        assertEquals(responseDto.getResponse().getNickname(), "testNickname");
//        assertEquals(Error.NOT_EXIST_USER.getMessage(),assertThrows(CustomException.class, () -> memberService.login(loginRequestDto, response)).getError().getMessage());
    }

    @Test
    @DisplayName("회원가입 중복체크")
    @Transactional
    void 가입중복() {
        // given
        memberRepository.save(member);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.memberCheck(member.getEmail()));

        // then
        assertEquals(Error.DUPLICATED_EMAIL.getMessage(), exception.getError().getMessage());
    }

    @Test
    @DisplayName("사업자 번호 중복체크")
    @Transactional
    void businessMemberCheck() {
        // given
        memberRepository.save(member);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.businessMemberCheck(member.getBusiness()));

        // then
        assertEquals(Error.DUPLICATED_BUSINESS.getMessage(), exception.getError().getMessage());
    }

    @Test
    @DisplayName("토큰 갱신 성공 테스트")
    @Transactional
    void issueToken() {
        // given
        memberRepository.save(member);
        MockHttpServletRequest  request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        TokenDto tokenDto = jwtUtil.createAllToken(member.getEmail());
        RefreshToken newToken = new RefreshToken(tokenDto.getRefresh_Token(), member.getEmail());
        refreshTokenRepository.save(newToken);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findAllByMemberId(member.getEmail());

        request.addHeader(JwtUtil.REFRESH_TOKEN, refreshToken.get().getRefreshToken());

        // when
        ApiResponseDto<SuccessResponse> responseDto = memberService.issueToken(request, response);
        // then
        assertEquals("토큰 갱신 성공.", responseDto.getResponse().getMessage());
    }

    @Test
    @DisplayName("히스토리 게시물")
    @Transactional
    void getMemberHistory() {
        memberRepository.save(member);
        String title = "히스토리 게시글1";
        String category = "히스토리 카테고리1";
        String ceo = "ceo";
        String contens = "컨텐츠";
        String lat = "1111.1111";
        String lng = "123.123";
        String address = "address";
        String telNum = "01000000000";
        String closedDay = "금요일";
        PostRequestDto postRequestDto = new PostRequestDto(title, category, ceo, contens, lat, lng, address, telNum, closedDay);
        Post post1 = new Post(postRequestDto, member, null, null);
        postRepository.save(post1);

        String title2 = "히스토리 게시글2";
        String category2 = "히스토리 카테고리2";

        PostRequestDto postRequestDto2 = new PostRequestDto(title2, category2, ceo, contens, lat, lng, address, telNum, closedDay);
        Post post2 = new Post(postRequestDto2, member, null, null);
        postRepository.save(post2);

        MemberHistory history1 = new MemberHistory(member, post1, new Date());
        memberHistoryRepository.save(history1);

        MemberHistory history2 = new MemberHistory(member, post2, new Date());
        memberHistoryRepository.save(history2);

        // when
        List<HistoryPostResponseDto> responseDtoList = memberService.getMemberHistory(member);

        // then
        assertEquals(2, responseDtoList.size());
        assertEquals("히스토리 게시글1", responseDtoList.get(1).getTitle());
        assertEquals("히스토리 카테고리1", responseDtoList.get(1).getCategory());
        assertEquals("히스토리 게시글2", responseDtoList.get(0).getTitle());
        assertEquals("히스토리 카테고리2", responseDtoList.get(0).getCategory());

    }
}