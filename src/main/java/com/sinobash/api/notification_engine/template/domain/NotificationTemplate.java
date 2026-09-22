package com.sinobash.api.notification_engine.template.domain;

import java.time.Instant;
import java.util.UUID;

import com.sinobash.api.notification_engine.notification.domain.NotificationChannel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity 
@Table(
    name = "notification_templates", 
    uniqueConstraints = @UniqueConstraint(name = "uk_template_code", columnNames = "code")
)
public class NotificationTemplate {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID) 
    private UUID id;

    @Column(nullable = false, length = 100) 
    private String code;

    @Enumerated(EnumType.STRING) 
    @Column(nullable = false, length = 20) 
    private NotificationChannel channel; 

    @Column(nullable = false, length = 200) 
    private String subject;

    @Column(nullable = false, columnDefinition = "text") 
    private String body; 

    @Column(nullable = false) 
    private boolean active = true; 

    @Column(name = "created_at", nullable = false) 
    private Instant createdAt = Instant.now();

    protected NotificationTemplate(){} 

    public NotificationTemplate(String code, NotificationChannel channel, String subject, String body) {
        this.code = code;
        this.channel = channel;
        this.subject = subject;
        this.body = body;
    }

    public String getCode() {
        return code;
    } 

    public NotificationChannel getChannel() {
        return channel;
    } 

    public String getSubject() {
        return subject;
    } 

    public String getBody() {
        return body;
    } 

    public boolean isActive() {
        return active;
    }

}
