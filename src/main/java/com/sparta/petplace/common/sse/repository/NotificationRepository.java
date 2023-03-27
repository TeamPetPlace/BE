package com.sparta.petplace.common.sse.repository;

import com.sparta.petplace.common.sse.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverIdOrderByIdDesc(Long memberId);

    Long countByReceiverIdAndIsReadFalse(Long userId);

    Optional<Notification> findById(Long NotificationId);

    void deleteAllByReceiverId(Long receiverId);

    void deleteById(Long notificationId);
}
