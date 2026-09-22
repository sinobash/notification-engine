package com.sinobash.api.notification_engine.admin.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sinobash.api.notification_engine.notification.domain.DeliveryAudit;
import com.sinobash.api.notification_engine.notification.infrastructure.DeliveryAuditRepository;

@RestController 
@RequestMapping("/api/v1/admin") 
public class AdminController {
    private final DeliveryAuditRepository audit; 
    
    public AdminController(DeliveryAuditRepository audit) {
        this.audit = audit;
    }

    @GetMapping("/audit/{notificationId}") 
    public List<DeliveryAudit> audit(@PathVariable UUID notificationId) {
        return audit.findByNotificationIdOrderByCreatedAtAsc(notificationId);
    }
    
    @GetMapping("/health") 
    public Map<String,String> health() {
        return Map.of("status","UP");
    }
}
