package com.sparta.petplace.member.dto;

import com.sparta.petplace.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponseDto {
    private String nickname;
    private String email;
    private String image;

    @Builder
    public MemberResponseDto(Member member) {
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.image = member.getImage();
    }
   public static MemberResponseDto from(Member member){
        return MemberResponseDto.builder()
                .member(member)
                .build();
   }
}
