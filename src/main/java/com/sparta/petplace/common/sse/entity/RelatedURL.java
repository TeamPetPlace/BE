package com.sparta.petplace.common.sse.entity;

import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.enumclass.Error;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor
public class RelatedURL {

    @Column(nullable = false, length = 255)
    private String url;

    public RelatedURL(String url) {
        if (ValidNoti(url)) {
            throw new CustomException(Error.NOT_VALIDCONTENT);
        }
        this.url = url;
    }

    private boolean ValidNoti(String url){
        return Objects.isNull(url) || url.length() > 255 || url.isEmpty();
    }
}
