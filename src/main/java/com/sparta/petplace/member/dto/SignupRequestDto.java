package com.sparta.petplace.member.dto;

import com.sparta.petplace.member.entity.Member;
import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class SignupRequestDto {
    // nickname 알파벳 대/소문자, 숫자, 한글, 언더바(_), 대시(-) 입력가능 2~20자
    @Pattern(regexp = "^[a-zA-Z0-9가-힣_-]{2,20}$", message = "닉네임에러")
    private String nickname;
    // password 암호가 적어도 8자 이상이어야하며 대문자, 소문자, 숫자 및 주어진 특수 문자 집합 중 하나 이상의 문자를 포함해야함
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "패스워드에러")
    private String password;
    // email 알파벳 숫자,하이폰,언덥바를 @를 기준으로 이전에 포함할 수 있고 @이후 도메인구조로 작성해야 한다. ex) naver.com
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", message = "이메일에러")
    private String email;

    public SignupRequestDto(Member member) {
        this.nickname = member.getNickname();
        this.password = member.getPassword();
        this.email = member.getEmail();
    }
}
