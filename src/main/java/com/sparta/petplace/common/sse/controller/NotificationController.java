package com.sparta.petplace.common.sse.controller;

import com.sparta.petplace.auth.security.UserDetailsImpl;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.sse.dto.NotificationCountDto;
import com.sparta.petplace.common.sse.dto.NotificationDto;
import com.sparta.petplace.common.sse.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // MINE TYPE - text/event - stream 형태로 받아야함
    // 클라이언트로부터 게시글 작성됨을 받는다
    // 로그인한 유저는 SSE 연결
    // lAST_EVENT_ID = 이전에 받지 못한 이벤트가 존재하는 경우 [SSE 시간 만료 혹은 종료]
    // 전달받은 마지막 ID 값을 넘겨 그 이후의 데이터[받지못한 데이터] 부터 받을수 있게 한다.
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "")
                            String lastEventId) {
        return notificationService.subscribe(userDetails.getMember().getId(), lastEventId);
    }


    @GetMapping(value = "/notifications")
    public List<NotificationDto> findAllNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.findAllNotifications(userDetails.getMember().getId());
    }


    @GetMapping(value = "/notifications/count")
    public NotificationCountDto countUnReadNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.countUnReadNotifications(userDetails.getMember().getId());
    }


    @DeleteMapping(value = "/notifications/delete")
    public ApiResponseDto<Object> deleteNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.deleteAllByNotifications(userDetails);
    }


    @DeleteMapping(value = "/notifications/delete/{notificationId}")
    public ApiResponseDto<Object> deleteNotifications(@PathVariable Long notificationId) {
        return notificationService.deleteByNotifications(notificationId);
    }
}
