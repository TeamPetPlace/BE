package com.sparta.petplace.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.ResponseUtils;
import com.sparta.petplace.common.SuccessLoginResponse;
import com.sparta.petplace.common.SuccessResponse;
import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.enumclass.Error;
import com.sparta.petplace.member.dto.BusinessSignupRequestDto;
import com.sparta.petplace.member.dto.LoginRequestDto;
import com.sparta.petplace.member.dto.SignupRequestDto;
import com.sparta.petplace.member.service.KakaoService;
import com.sparta.petplace.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final KakaoService kakaoService;
    /**
     * 회원가입 기능 Controller
     */

    @PostMapping("/signup")
    public ApiResponseDto<SuccessResponse> signup(@Valid @RequestBody SignupRequestDto signupRequestDto,
                                                  BindingResult result) {
        if (result.hasErrors()){
            if (result.getFieldError().getDefaultMessage().equals("패스워드에러"))
                throw new CustomException(Error.WRONG_PASSWORD_CHECK);
            if (result.getFieldError().getDefaultMessage().equals("닉네임에러"))
                throw new CustomException(Error.VALIDATE_NICKNAME_ERROR);
            if (result.getFieldError().getDefaultMessage().equals("이메일에러"))
                throw new CustomException(Error.VALIDATE_EMAIL_ERROR);
        }
        return memberService.signup(signupRequestDto);
    }
    /**
     * 사업자 회원가입
     **/
    @PostMapping("/business_signup")
    public ApiResponseDto<SuccessResponse> businessSignup(@Valid @RequestBody BusinessSignupRequestDto signupRequestDto,
                                                  BindingResult result) {
        if (result.hasErrors()){
            if (result.getFieldError().getDefaultMessage().equals("패스워드에러"))
                throw new CustomException(Error.WRONG_PASSWORD_CHECK);
            if (result.getFieldError().getDefaultMessage().equals("사업자 등록번호 에러"))
                throw new CustomException(Error.VALIDATE_BUSINESS);
            if (result.getFieldError().getDefaultMessage().equals("닉네임에러"))
                throw new CustomException(Error.VALIDATE_NICKNAME_ERROR);
            if (result.getFieldError().getDefaultMessage().equals("이메일에러"))
                throw new CustomException(Error.VALIDATE_EMAIL_ERROR);
        }
        return memberService.businessSignup(signupRequestDto);
    }

    /**
     * 로그인 메서드
     **/
    @PostMapping("/login")
    public ApiResponseDto<SuccessLoginResponse> login(@RequestBody LoginRequestDto requestDto,
                                                      HttpServletResponse response){
        return memberService.login(requestDto,response);
    }

    /**
     * 회원명 중복 체크
     */

    @GetMapping("/signup")
    public ApiResponseDto<SuccessResponse> memberCheck( @RequestParam("email") String email) {
        return   memberService.memberCheck(email);
    }


    /**
     * 사업자명 중복체크
     */
    @GetMapping("/signup/businesscheck")
    public ApiResponseDto<SuccessResponse> businessMemberCheck( @RequestParam("email") String email) {
        return  memberService.businessMemberCheck(email);
    }


    /**
     * 회원 토큰 갱신
     **/
    @GetMapping("/token")
    public  ApiResponseDto<SuccessResponse> issuedToken(HttpServletRequest request,
                                                        HttpServletResponse response){
        return memberService.issueToken(request,response);
    }

    @GetMapping("/kakao/callback")
    public ApiResponseDto<SuccessResponse> kakaoLogin(@RequestParam String code, HttpServletResponse response)throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, response);
    }

}
