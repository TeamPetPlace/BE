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
public class Category {

    @Column(nullable = false, length = 255)
    private String category;

    public Category(String category) {
        if (ValidNoti(category)) {
            throw new CustomException(Error.NOT_VALIDCONTENT);
        }
        this.category = category;
    }

    private boolean ValidNoti(String category){
        return Objects.isNull(category) || category.length() > 255 || category.isEmpty();
    }
}
