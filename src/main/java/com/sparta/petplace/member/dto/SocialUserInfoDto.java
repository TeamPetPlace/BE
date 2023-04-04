package com.sparta.petplace.member.dto;

import com.sparta.petplace.member.entity.LoginType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SocialUserInfoDto {
    private String email;
    private String nickname;
    private LoginType loginType;

    @Builder
    public SocialUserInfoDto(String nickname, String email, LoginType loginType) {
        this.nickname = nickname;
        this.email = email;
        this.loginType = loginType;
    }

    public static SocialUserInfoDto of(String nickname, String email, LoginType loginType){
        return SocialUserInfoDto.builder()
                .nickname(nickname)
                .email(email)
                .loginType(loginType)
                .build();
    }
}