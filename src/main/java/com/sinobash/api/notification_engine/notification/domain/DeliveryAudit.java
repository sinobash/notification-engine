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

@Entity 
@Table(name="delivery_audit")
public class DeliveryAudit {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID) 
    private UUID id;
    
    @Column(name = "notification_id", nullable = false) 
    private UUID notificationId;
    
    @Enumerated(EnumType.STRING) 
    @Column(name = "from_status", length = 20) 
    private NotificationStatus fromStatus;
    
    @Enumerated(EnumType.STRING) 
    @Column(name = "to_status", nullable = false, length = 20) 
    private NotificationStatus toStatus;
    
    @Column(columnDefinition = "text") 
    private String detail;
    
    @Column(name = "created_at", nullable = false) 
    private Instant createdAt;
    
    protected DeliveryAudit() {}
    
    public DeliveryAudit(UUID notificationId, NotificationStatus fromStatus, NotificationStatus toStatus, String detail) {
        this.notificationId = notificationId; 
        this.fromStatus = fromStatus; 
        this.toStatus = toStatus; 
        this.detail = detail; 
        this.createdAt = Instant.now();
    }
    
    public UUID getId() {
        return id;
    } 
    
    public UUID getNotificationId() {
        return notificationId;
    } 
    
    public NotificationStatus getFromStatus() {
        return fromStatus;
    }
    
    public NotificationStatus getToStatus() {
        return toStatus;
    } 
    
    public String getDetail() {
        return detail;
    } 
    
    public Instant getCreatedAt() {
        return createdAt;
    }
}
