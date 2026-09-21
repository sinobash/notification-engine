package com.sinobash.api.notification_engine.notification.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinobash.api.notification_engine.notification.domain.DeliveryAudit;

public interface DeliveryAuditRepository extends JpaRepository<DeliveryAudit, UUID> {
    List<DeliveryAudit> findByNotificationIdOrderByCreatedAtAsc(UUID notificationId);
}
