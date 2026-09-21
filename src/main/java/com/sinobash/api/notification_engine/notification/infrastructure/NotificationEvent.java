package com.sinobash.api.notification_engine.notification.infrastructure;

import java.util.UUID;

import com.sinobash.api.notification_engine.notification.domain.NotificationChannel;
import com.sinobash.api.notification_engine.notification.domain.NotificationPriority;

public record NotificationEvent(UUID notificationId, String idempotencyKey, NotificationChannel channel, NotificationPriority priority) {

}
