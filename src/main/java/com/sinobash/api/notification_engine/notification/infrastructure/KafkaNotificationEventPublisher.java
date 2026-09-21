package com.sinobash.api.notification_engine.notification.infrastructure;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.sinobash.api.notification_engine.notification.domain.Notification;

@Component 
public class KafkaNotificationEventPublisher implements NotificationEventPublisher {
    public static final String HIGH = "notifications.high", LOW = "notifications.low", DLQ = "notifications.dlq"; 
    private final KafkaTemplate<String, NotificationEvent> kafka;

    public KafkaNotificationEventPublisher(KafkaTemplate<String, NotificationEvent> kafka) {
        this.kafka = kafka;
    }

    @Override
    public void publish(Notification n) {
        kafka.send(
            n.getPriority().name().equals("HIGH") ? HIGH : LOW, 
            n.getId().toString(), new NotificationEvent(
                n.getId(), 
                n.getIdempotencyKey(), 
                n.getChannel(), 
                n.getPriority()
            )
        );
    }

    @Override
    public void publishDlq(Notification n) {
        kafka.send(
            DLQ,
            n.getId().toString(),
            new NotificationEvent(
                n.getId(),
                n.getIdempotencyKey(),
                n.getChannel(),
                n.getPriority()
            )
        );
    }

}
