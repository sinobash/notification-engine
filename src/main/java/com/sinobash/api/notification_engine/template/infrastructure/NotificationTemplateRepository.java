package com.sinobash.api.notification_engine.template.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinobash.api.notification_engine.template.domain.NotificationTemplate;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {
    Optional<NotificationTemplate> findByCodeAndActiveTrue(String code);
}
