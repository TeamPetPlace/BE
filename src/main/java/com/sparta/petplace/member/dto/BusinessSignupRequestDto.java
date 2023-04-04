package com.sparta.petplace.member.dto;

import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class BusinessSignupRequestDto {
    @Pattern(regexp = "^[a-zA-Z0-9가-힣_-]{2,20}$", message = "닉네임에러")
    private String nickname;
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "패스워드에러")
    private String password;
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", message = "이메일에러")
    private String email;
    @Pattern(regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{5}$", message = "사업자 등록번호 에러")
    private String business;
}
