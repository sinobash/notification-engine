package com.sinobash.api.notification_engine.notification.infrastructure;

import com.sinobash.api.notification_engine.notification.domain.NotificationChannel;

public interface ChannelDispatcher {
    void dispatch(NotificationChannel channel, String recipient, String body);
}
