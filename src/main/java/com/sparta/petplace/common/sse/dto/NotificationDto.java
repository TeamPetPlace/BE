package com.sparta.petplace.common.sse.dto;

import com.sparta.petplace.common.sse.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;

    private String content;

    private String url;

    private Boolean status;

    public static NotificationDto create(Notification notification) {
        return new NotificationDto(notification.getId(), notification.getContent(), notification.getUrl(), notification.getIsRead());
    }
}
