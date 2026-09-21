package com.sinobash.api.notification_engine.notification.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name="notifications", 
    indexes = {
        @Index(name = "ix_notifications_user_created", columnList = "user_id,created_at"),
        @Index(name = "ix_notifications_status", columnList = "status")
    }, 
    uniqueConstraints = @UniqueConstraint(name = "uk_notifications_idempotency", columnNames = "idempotency_key")
)
public class Notification {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "idempotency_key", nullable = false, length = 200) 
    private String idempotencyKey;
    
    @Column(name = "user_id", nullable = false, length = 200) 
    private String userId;
    
    @Enumerated(EnumType.STRING) 
    @Column(nullable = false, length = 20) 
    private NotificationChannel channel;
    
    @Enumerated(EnumType.STRING) 
    @Column(nullable = false, length = 20) 
    private NotificationPriority priority;
    
    @Enumerated(EnumType.STRING) 
    @Column(nullable = false, length = 20) 
    private NotificationStatus status;
    
    @Column(nullable = false, length = 500) 
    private String recipient;
    
    @Column(name = "template_code", length = 100) 
    private String templateCode;
    
    @Column(columnDefinition = "text") 
    private String body;
    
    @Column(columnDefinition = "jsonb") 
    private String templateParams;
    
    @Column(columnDefinition = "jsonb") 
    private String metadata;
    
    @Column(name = "retry_count", nullable = false) 
    private int retryCount;
    
    @Column(name = "last_error", columnDefinition = "text") 
    private String lastError;
    
    @Column(name = "created_at", nullable = false) 
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false) 
    private Instant updatedAt;

    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    protected Notification() {}
    
    public Notification(String idempotencyKey, String userId, NotificationChannel channel, NotificationPriority priority,
                         String recipient, String templateCode, String body, String templateParams, String metadata) {
        this.idempotencyKey = idempotencyKey; 
        this.userId = userId; 
        this.channel = channel; 
        this.priority = priority;
        this.recipient = recipient; 
        this.templateCode = templateCode; 
        this.body = body; 
        this.templateParams = templateParams; 
        this.metadata = metadata;
        this.status = NotificationStatus.QUEUED; 
        this.createdAt = Instant.now(); 
        this.updatedAt = this.createdAt;
    }
    
    public void transitionTo(NotificationStatus next) { 
        this.status = next; 
        this.updatedAt = Instant.now(); 
    }
    
    public void fail(String error) { 
        this.status = NotificationStatus.FAILED; 
        this.lastError = error; 
        this.updatedAt = Instant.now(); 
    }
    
    public void retry() { 
        this.retryCount++; 
        this.status = NotificationStatus.QUEUED; 
        this.nextAttemptAt = Instant.now().plusSeconds((long)Math.min(60, Math.pow(2, this.retryCount-1))); 
        this.updatedAt = Instant.now(); 
    }
    
    public UUID getId() {
        return id;
    } 
    
    public String getIdempotencyKey() {
        return idempotencyKey;
    } 
    
    public String getUserId() {
        return userId;
    }
    
    public NotificationChannel getChannel() {
        return channel;
    } 
    
    public NotificationPriority getPriority() {
        return priority;
    } 
    
    public NotificationStatus getStatus() {
        return status;
    }
    
    public String getRecipient() {
        return recipient;
    } 
    
    public String getTemplateCode() {
        return templateCode;
    } 
    
    public String getBody() {
        return body;
    }
    
    public String getTemplateParams() {
        return templateParams;
    } 
    
    public String getMetadata() {
        return metadata;
    } 
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public String getLastError() {
        return lastError;
    } 
    
    public Instant getCreatedAt() {
        return createdAt;
    } 
    
    public Instant getUpdatedAt() {
        return updatedAt;
    } 
    
    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }
}
