package com.sinobash.api.notification_engine.notification.application;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sinobash.api.notification_engine.notification.domain.DeliveryAudit;
import com.sinobash.api.notification_engine.notification.domain.Notification;
import com.sinobash.api.notification_engine.notification.domain.NotificationStatus;
import com.sinobash.api.notification_engine.notification.infrastructure.ChannelDispatcher;
import com.sinobash.api.notification_engine.notification.infrastructure.DeliveryAuditRepository;
import com.sinobash.api.notification_engine.notification.infrastructure.KafkaNotificationEventPublisher;
import com.sinobash.api.notification_engine.notification.infrastructure.NotificationEvent;
import com.sinobash.api.notification_engine.notification.infrastructure.NotificationEventPublisher;
import com.sinobash.api.notification_engine.notification.infrastructure.NotificationRepository;
import com.sinobash.api.notification_engine.template.application.TemplateRenderer;

@Component 
public class NotificationConsumer {
    private final NotificationRepository repository; 
    private final DeliveryAuditRepository audit; 
    private final StringRedisTemplate redis; 
    private final TemplateRenderer renderer; 
    private final ChannelDispatcher dispatcher; 
    private final NotificationEventPublisher publisher;

    public NotificationConsumer(
        NotificationRepository repository, 
        DeliveryAuditRepository audit, 
        StringRedisTemplate redis, 
        TemplateRenderer renderer, 
        ChannelDispatcher dispatcher, 
        NotificationEventPublisher publisher
    ) {
        this.repository = repository;
        this.audit = audit;
        this.redis = redis;
        this.renderer = renderer;
        this.dispatcher = dispatcher;
        this.publisher = publisher;
    }
    
    @KafkaListener(
        topics = KafkaNotificationEventPublisher.HIGH, 
        groupId = "notification-workers", 
        concurrency = "3"
    ) 
    public void high(NotificationEvent e) {
        process(e);
    }
    
    @KafkaListener(
        topics = KafkaNotificationEventPublisher.LOW, 
        groupId = "notification-workers", 
        concurrency = "2"
    ) 
    public void low(NotificationEvent e) {
        process(e);
    }
    
    @Transactional
    public void process(NotificationEvent event) {
        Notification n = repository.findById(event.notificationId()).orElse(null); 
        if ( n == null ) return;

        String lock = "notification:processing:" + n.getIdempotencyKey(); 
        Boolean claimed = redis.opsForValue().setIfAbsent(lock,"1", Duration.ofMinutes(10)); 
        if(Boolean.FALSE.equals(claimed)) return;
        try {
            var old = n.getStatus(); 
            n.transitionTo(NotificationStatus.DISPATCHED); 
            repository.save(n); 
            audit.save(new DeliveryAudit(n.getId(), old, n.getStatus(), "dispatching"));
            
            String body = renderer.render(n);
            dispatcher.dispatch(n.getChannel(), n.getRecipient(), body);
            
            old = n.getStatus(); 
            n.transitionTo(NotificationStatus.DELIVERED); 
            repository.save(n); 
            audit.save(new DeliveryAudit(n.getId(), old, n.getStatus(), "delivered"));
        } catch(Exception ex) { 
            handleFailure(n, ex); 
        } finally { 
            redis.delete(lock); 
        }
    }
    
    private void handleFailure(Notification n, Exception ex){
        if ( n.getRetryCount() < 5) { 
            var old = n.getStatus(); 
            n.retry(); 
            repository.save(n); 
            audit.save(new DeliveryAudit(n.getId(), old, n.getStatus(), "retry: " + ex.getMessage())); 
        }
        else { 
            var old = n.getStatus(); 
            n.fail(ex.getMessage()); 
            repository.save(n); 
            audit.save(new DeliveryAudit(n.getId(), old, n.getStatus(), "exhausted retries")); 
            publisher.publishDlq(n); 
        }
    }
}
