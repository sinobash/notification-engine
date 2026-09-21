package com.sinobash.api.notification_engine.notification.application;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinobash.api.notification_engine.notification.domain.Notification;
import com.sinobash.api.notification_engine.notification.domain.NotificationStatus;
import com.sinobash.api.notification_engine.notification.api.NotificationRequest;
import com.sinobash.api.notification_engine.notification.api.NotificationResponse;
import com.sinobash.api.notification_engine.notification.domain.DeliveryAudit;
import com.sinobash.api.notification_engine.notification.infrastructure.DeliveryAuditRepository;
import com.sinobash.api.notification_engine.notification.infrastructure.NotificationEventPublisher;
import com.sinobash.api.notification_engine.notification.infrastructure.NotificationRepository;
import com.sinobash.api.notification_engine.shared.error.NotFoundException;

import tools.jackson.databind.json.JsonMapper;

@Service
public class NotificationService {
    private final NotificationRepository repository; 
    private final DeliveryAuditRepository audit; 
    private final NotificationEventPublisher publisher; 
    private final JsonMapper mapper;
    
    public NotificationService(NotificationRepository repository, DeliveryAuditRepository audit, NotificationEventPublisher publisher, JsonMapper mapper) {
        this.repository = repository;
        this.audit = audit;
        this.publisher = publisher;
        this.mapper = mapper;
    }

    @Transactional
    public NotificationResponse create(NotificationRequest r) {
        var existing = repository.findByIdempotencyKey(r.idempotencyKey()); 
        if(existing.isPresent()) return NotificationResponse.from(existing.get());

        try {
            var n = new Notification(
                        r.idempotencyKey(),
                        r.userId(),
                        r.channel(),
                        r.priority(),
                        r.recipient(),
                        r.templateCode(),
                        r.body(),
                        mapper.writeValueAsString(
                            r.templateParams() == null ? Map.of() : r.templateParams()
                        ),
                        mapper.writeValueAsString(
                            r.metadata() == null ? Map.of() : r.metadata()
                        )
                    );
            n = repository.save(n); 
            audit.save(new DeliveryAudit(n.getId(), null, NotificationStatus.QUEUED, "accepted")); 
            publisher.publish(n); 
            return NotificationResponse.from(n);
        } catch(Exception e) { 
            throw new IllegalArgumentException("Invalid notification payload", e); 
        }
    }
    
    @Transactional(readOnly = true) 
    public NotificationResponse get(UUID id) {
        return repository.findById(id).map(NotificationResponse::from).orElseThrow(() -> new NotFoundException("Notification " + id + " not found"));
    }
    
    @Transactional(readOnly = true) 
    public NotificationResponse getByKey(String key) {
        return repository.findByIdempotencyKey(key).map(NotificationResponse::from).orElseThrow(() -> new NotFoundException("Notification key not found"));
    }
    
    @Transactional(readOnly = true) 
    public Page<NotificationResponse> byUser(String userId, Pageable pageable) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(NotificationResponse::from);
    }
}
