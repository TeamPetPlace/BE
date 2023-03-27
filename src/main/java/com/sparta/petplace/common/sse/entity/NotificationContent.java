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
public class NotificationContent {

    private static final int Max_LENGTH = 100;

    @Column(nullable = false, length = Max_LENGTH)
    private String content;

    public NotificationContent(String content){
        if (ValidNotify(content)){
            throw new CustomException(Error.NOT_VALIDCONTENT);
        }
        this.content = content;
    }

    private boolean ValidNotify(String content){
        return Objects.isNull(content) || content.length() > Max_LENGTH || content.isEmpty();
    }
}
