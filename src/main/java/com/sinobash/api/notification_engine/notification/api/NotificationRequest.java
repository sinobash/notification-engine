package com.sinobash.api.notification_engine.notification.api;

import java.util.Map;

import com.sinobash.api.notification_engine.notification.domain.NotificationChannel;
import com.sinobash.api.notification_engine.notification.domain.NotificationPriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
    @NotBlank 
    String idempotencyKey, 
    @NotBlank 
    String userId, 
    @NotNull 
    NotificationChannel channel,
    @NotNull 
    NotificationPriority priority, 
    @NotBlank 
    String recipient, 
    String templateCode, 
    String body,
    Map<String, Object> templateParams, 
    Map<String, Object> metadata
) {}
