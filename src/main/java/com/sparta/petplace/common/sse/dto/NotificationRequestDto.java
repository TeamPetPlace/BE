package com.sparta.petplace.common.sse.dto;

import com.sparta.petplace.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {
    private Member receiver;
    private String content;
    private String url;
}
