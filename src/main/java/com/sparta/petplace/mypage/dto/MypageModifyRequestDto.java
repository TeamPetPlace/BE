package com.sparta.petplace.mypage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@NoArgsConstructor
public class MypageModifyRequestDto {
    private String nickname;
    private MultipartFile image;
}
