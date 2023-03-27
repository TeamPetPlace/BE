package com.sparta.petplace.common.sse.service;

import com.sparta.petplace.auth.security.UserDetailsImpl;
import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.ResponseUtils;
import com.sparta.petplace.common.SuccessResponse;
import com.sparta.petplace.common.sse.dto.NotificationCountDto;
import com.sparta.petplace.common.sse.dto.NotificationDto;
import com.sparta.petplace.common.sse.entity.Notification;
import com.sparta.petplace.common.sse.repository.EmitterRepository;
import com.sparta.petplace.common.sse.repository.EmitterRepositoryImpl;
import com.sparta.petplace.common.sse.repository.NotificationRepository;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();
    private final MemberRepository memberRepository;

    // SSE 연결 설정
    public SseEmitter subscribe(Long memberId, String lastEventId) {
        // emitter 에 고유값을 주기위해
        String emitterId = makeTimeIncludeUd(memberId);

        Long timeout = 10L * 1000L * 60L; // 10분
        // 생성된 emitterId를 기반으로 emitter 를 저장
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(timeout));

        // 전달이 완료되거나 emitter의 시간이 만료된 후 레포에서 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));


        // 503 에러를 방지하기 위해 처음 연결 진행 시 더미 데이터를 전달
        String eventId = makeTimeIncludeUd(memberId);

        // 수 많은 이벤트 들을 구분하기 위해 이벤트 ID 를 시간을 통해 구분해줌
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + memberId + "]");

        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, memberId, emitterId, emitter);
        }

        return emitter;
    }

    // 게시글에 리뷰 작성시 알림 보내는 기능
    @Async
    public void send(Member receiver, String content, String url){
        Notification notification = notificationRepository.save(createNotification(receiver, content, url));

        Long receiverId = receiver.getId();
        String eventId = makeTimeIncludeUd(receiverId);
        Map<String, SseEmitter> emitterMap = emitterRepository.findAllEmitterStartWithByMemberId(String.valueOf(receiverId));
        emitterMap.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, emitter);
                    sendNotification(emitter, eventId, key, NotificationDto.create(notification));
                }
        );

    }

    // 모든 알림 조회
    @Transactional
    public List<NotificationDto> findAllNotifications(Long memberId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByIdDesc(memberId);
        return notifications.stream()
                .map(NotificationDto::create)
                .collect(Collectors.toList());
    }

    // 읽지않은 알림의 개수 조회
    public NotificationCountDto countUnReadNotifications(Long memberId) {
        // 유저의 알람리스트에서 -> isRead(false)인 갯수를 측정.
        Long count = notificationRepository.countByReceiverIdAndIsReadFalse(memberId);
        return NotificationCountDto.builder()
                .count(count)
                .build();
    }

    // 알림 전체 삭제
    @Transactional
    public ApiResponseDto<Object> deleteAllByNotifications(UserDetailsImpl userDetails) {
        Long receiverId = userDetails.getMember().getId();
        notificationRepository.deleteAllByReceiverId(receiverId);
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "알림 전체 삭제 성공"));
    }

    // 알림 삭제
    @Transactional
    public ApiResponseDto<Object> deleteByNotifications(Long notificationId) {
        notificationRepository.deleteById(notificationId);
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "알림 삭제 성공"));
    }


    // ==================================== Method Extract ====================================

    // 알림 객체 생성
    private Notification createNotification(Member receiver, String content, String url) {
        return Notification.builder()
                .receiver(receiver)
                .content(content)
                .url(url)
                .isRead(false)
                .build();
    }

    // lastEventId 이후에 발생한 알림을 전송
    private void sendLostData(String lastEventId, Long memberId, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    // lastEventId가 비어있는지 확인
    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    // 데이터 전송
    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    // emitterId 고유값만드는 메서드
    private String makeTimeIncludeUd(Long memberId) {
        return memberId + "_" + System.currentTimeMillis();
    }
}
