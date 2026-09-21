package com.sinobash.api.notification_engine.notification.api;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record BatchNotificationRequest(
    @NotEmpty 
    List<@Valid NotificationRequest> notifications
) {}
