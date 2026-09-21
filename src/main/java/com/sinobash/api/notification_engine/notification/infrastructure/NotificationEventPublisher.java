package com.sinobash.api.notification_engine.notification.infrastructure;

import com.sinobash.api.notification_engine.notification.domain.Notification;

public interface NotificationEventPublisher {
    void publish(Notification notification); 
    void publishDlq(Notification notification);
}
