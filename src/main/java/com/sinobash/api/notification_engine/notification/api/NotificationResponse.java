package com.sinobash.api.notification_engine.notification.api;

import java.time.Instant;
import java.util.UUID;

import com.sinobash.api.notification_engine.notification.domain.Notification;
import com.sinobash.api.notification_engine.notification.domain.NotificationChannel;
import com.sinobash.api.notification_engine.notification.domain.NotificationPriority;
import com.sinobash.api.notification_engine.notification.domain.NotificationStatus;

public record NotificationResponse(
    UUID id, 
    String idempotencyKey, 
    String userId, 
    NotificationChannel channel, 
    NotificationPriority priority,
    NotificationStatus status, 
    String recipient, 
    int retryCount, 
    String lastError, 
    Instant createdAt, 
    Instant updatedAt
) {
    
    public static NotificationResponse from(Notification n) { 
        return new NotificationResponse(
            n.getId(),
            n.getIdempotencyKey(),
            n.getUserId(),
            n.getChannel(),
            n.getPriority(),
            n.getStatus(),
            n.getRecipient(),
            n.getRetryCount(),
            n.getLastError(),
            n.getCreatedAt(),
            n.getUpdatedAt()
        ); 
    }
}
