package com.sinobash.api.notification_engine.notification.application;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sinobash.api.notification_engine.notification.domain.NotificationStatus;
import com.sinobash.api.notification_engine.notification.infrastructure.NotificationEventPublisher;
import com.sinobash.api.notification_engine.notification.infrastructure.NotificationRepository;

@Component
public class RetryScheduler {
    private final NotificationRepository repository; 
    private final NotificationEventPublisher publisher;
    
    public RetryScheduler(NotificationRepository repository, NotificationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }
    
    @Scheduled(fixedDelayString = "${notification.retry.scheduler-delay:1000ms}")
    @Transactional
    public void publishDueRetries() {
        repository.findByStatusAndRetryCountGreaterThanAndNextAttemptAtLessThanEqual(NotificationStatus.QUEUED, 0, Instant.now())
            .forEach(publisher::publish);
    }
}
