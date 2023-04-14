package com.sparta.petplace.common.sse.entity;

import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.Error;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor
public class PostId {

    @Column(nullable = false, length = 255)
    private String postId;

    public PostId(String postId) {
        if (ValidNoti(postId)) {
            throw new CustomException(Error.NOT_VALIDCONTENT);
        }
        this.postId = postId;
    }

    private boolean ValidNoti(String postId){
        return Objects.isNull(postId) || postId.length() > 255 || postId.isEmpty();
    }
}
