package com.sparta.petplace.member.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;
    @Column(nullable = true)
    private String business;
    @Column(nullable = true)
    private String image;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private LoginType loginType;



    @Builder
    public Member(String email,String password, String nickname, String business,String image,LoginType loginType){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.business = business;
        this.image = image;
        this.loginType = loginType;
    }

    public void updateLoginStatus(LoginType loginType){
        this.loginType = loginType;
    }

    public static Member of (String email, String password, String nickname, String business, LoginType loginType,String image ) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .business(business)
                .image(image)
                .loginType(loginType)
                .build();
    }

    public void update(String nickname){
        this.nickname =nickname;
    }

    public void update(String nickname, String image) {
        this.nickname = nickname;
        this.image = image;
    }

}
