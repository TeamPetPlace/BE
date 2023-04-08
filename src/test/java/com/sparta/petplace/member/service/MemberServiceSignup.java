package com.sparta.petplace.member.service;

import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.SuccessResponse;
import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.Error;
import com.sparta.petplace.member.dto.SignupRequestDto;
import com.sparta.petplace.member.entity.LoginType;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.member.repository.MemberRepository;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class MemberServiceSignup {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void 회원가입테스트(){
//        given
        SignupRequestDto requestDto = new SignupRequestDto();
        requestDto.setNickname("nickname1");
        requestDto.setEmail("email1@example.com");
        requestDto.setPassword("Abcd1234!");

//        when
        ApiResponseDto<SuccessResponse> responseDto = memberService.signup(requestDto);

//        then
        assertEquals(HttpStatus.SC_OK, responseDto.getResponse().getStatus());
        assertEquals("회원가입 성공",responseDto.getResponse().getMessage());

        Member savedMember = memberRepository.findByEmail(requestDto.getEmail()).orElse(null);
        assertNotNull(savedMember);
        assertEquals(requestDto.getNickname(), savedMember.getNickname());
        assertEquals(requestDto.getEmail(),savedMember.getEmail());
        assertTrue(passwordEncoder.matches(requestDto.getPassword(), savedMember.getPassword() ));
    }

//    회원가입 실패
    @Test
    public void 중복체크예외발생(){
//        given
        SignupRequestDto requestDto = new SignupRequestDto();
        requestDto.setNickname("nickname1");
        requestDto.setEmail("email@emample.com");
        requestDto.setPassword("Abcd1234!");

        Member member = Member.builder()
                .nickname("nickname1")
                .email("email@emample.com")
                .password("Abcd1234!")
                .loginType(LoginType.USER)
                .build();
        memberRepository.save(member);

//        when
        Exception exception = assertThrows(CustomException.class, () ->
        memberService.signup(requestDto)
        );
        assertEquals(exception.getMessage(), Error.DUPLICATED_EMAIL.getMessage());

//        then
//        DuplicateEmailException 발생
    }

    @Test()
    public void 유효성검사예외발생(){
//        given
        SignupRequestDto requestDto = new SignupRequestDto();
        requestDto.setNickname("nn");                               //  닉네임 기준 미달
        requestDto.setEmail("email1");
        requestDto.setPassword("Abcd1234!");

//        when
        memberService.signup(requestDto);

//        then

//        ConstraintViolationException 발생

    }



}