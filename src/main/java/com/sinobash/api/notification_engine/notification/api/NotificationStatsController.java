package com.sinobash.api.notification_engine.notification.api;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sinobash.api.notification_engine.notification.domain.NotificationStatus;
import com.sinobash.api.notification_engine.notification.infrastructure.NotificationRepository;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationStatsController {
    private final NotificationRepository repository;

    public NotificationStatsController(NotificationRepository repository) {
        this.repository = repository;
    }
    
    @GetMapping("/stats")
    public Map<NotificationStatus, Long> stats() {
        var result = new EnumMap<NotificationStatus, Long>(NotificationStatus.class);
        for(var status: NotificationStatus.values()) result.put(status,repository.countByStatus(status));
        return result;
    }
}
