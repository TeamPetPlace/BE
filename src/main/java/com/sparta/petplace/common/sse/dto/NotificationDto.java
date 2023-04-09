package com.sparta.petplace.common.sse.dto;

import com.sparta.petplace.common.sse.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;

    private String content;

    private String postId;
    private String url;

    private Boolean status;
    private String category;
    private LocalDateTime createdAt;

    private String nickname;

    public static NotificationDto create(Notification notification) {
        return new NotificationDto(notification.getId(), notification.getContent(), notification.getPostId(), notification.getUrl(), notification.getIsRead(), notification.getCategory().getCategory(),  notification.getCreatedAt(), notification.getReceiver().getNickname());
    }
}
