package com.sinobash.api.notification_engine.notification.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sinobash.api.notification_engine.notification.domain.Notification;
import com.sinobash.api.notification_engine.notification.domain.NotificationStatus;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Optional<Notification> findByIdempotencyKey(String key);
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    long countByStatus(NotificationStatus status);
    List<Notification> findByStatusAndRetryCountGreaterThanAndNextAttemptAtLessThanEqual(NotificationStatus status, int retryCount, Instant now);
}
