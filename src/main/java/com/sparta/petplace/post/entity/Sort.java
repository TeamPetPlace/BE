package com.sparta.petplace.post.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Sort {
    STAR("별점"),       //  별점
    REVIEW("리뷰"),       //  리뷰
    DISTANCE("거리");     //  거리

    private final String type;
}
