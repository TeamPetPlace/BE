package com.sparta.petplace.member.dto;

import com.sparta.petplace.member.entity.LoginType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    private String nickname;
    private LoginType loginType;
    private SocialUserInfoDto socialUserInfoDto;
    private String img;


    @Builder
    public LoginResponseDto(String nickname, LoginType loginType, SocialUserInfoDto socialUserInfoDto, String img) {
        this.loginType = loginType;
        this.nickname = nickname;
        this.socialUserInfoDto = socialUserInfoDto;
        this.img = img;

    }

    public static LoginResponseDto of(String nickname, LoginType loginType) {
       return LoginResponseDto.builder()
               .loginType(loginType)
               .nickname(nickname)
               .build();
    }
}

