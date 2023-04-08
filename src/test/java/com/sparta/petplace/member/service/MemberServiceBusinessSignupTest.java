package com.sparta.petplace.member.service;

import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.SuccessResponse;
import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.Error;
import com.sparta.petplace.member.dto.BusinessSignupRequestDto;
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

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class MemberServiceBusinessSignupTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void 사업자회원가입(){
//        given
        BusinessSignupRequestDto requestDto = new BusinessSignupRequestDto();
        requestDto.setNickname("nickname2");
        requestDto.setEmail("email2@example.com");
        requestDto.setPassword("Abcd1234!");
        requestDto.setBusiness("110-31-15621");
//        when
        ApiResponseDto<SuccessResponse> responseDto = memberService.businessSignup(requestDto);
//        then
        assertEquals(HttpStatus.SC_OK,responseDto.getResponse().getStatus());
        assertEquals("회원가입 성공",responseDto.getResponse().getMessage());

        Member savedMember = memberRepository.findByBusiness(requestDto.getBusiness()).orElse(null);
        assertNotNull(savedMember);
        assertEquals(requestDto.getNickname(),savedMember.getNickname());
        assertEquals(requestDto.getEmail(),savedMember.getEmail());
        assertEquals(requestDto.getBusiness(),savedMember.getBusiness());
        assertTrue(passwordEncoder.matches(requestDto.getPassword(), savedMember.getPassword()));
    }

//    회원가입 실패
    @Test
    public void 이메일중복체크예외발생(){
//        given
        BusinessSignupRequestDto requestDto = new BusinessSignupRequestDto();
        requestDto.setNickname("nickname2");
        requestDto.setEmail("email2@example.com");
        requestDto.setPassword("Abcd1234!");
        requestDto.setBusiness("110-31-15621");

        Member member = Member.builder()
                .nickname("nickname2")
                .email("email2@example.com")
                .password("Abcd1234!")
                .business("110-31-15621")
                .loginType(LoginType.BUSINESS)
                .build();
        memberRepository.save(member);
//        when
        Exception exception = assertThrows(CustomException.class,() ->
                memberService.businessSignup(requestDto)
                );
        assertEquals(exception.getMessage(), Error.DUPLICATED_EMAIL.getMessage());
//        then
//        DuplicateEmailException 발생
    }


}