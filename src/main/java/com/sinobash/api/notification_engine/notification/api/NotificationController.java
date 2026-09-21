package com.sinobash.api.notification_engine.notification.api;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sinobash.api.notification_engine.notification.application.NotificationService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService service; 
    
    public NotificationController(NotificationService service) {
        this.service = service;
    }
    
    @PostMapping 
    public ResponseEntity<NotificationResponse> send(@Valid @RequestBody NotificationRequest r) { 
        return ResponseEntity.accepted().body(service.create(r)); 
    }
    
    @PostMapping("/batch") 
    public ResponseEntity<List<NotificationResponse>> batch(@Valid @RequestBody BatchNotificationRequest r) { 
        return ResponseEntity.accepted().body(r.notifications().stream().map(service::create).toList()); 
    }
    
    @GetMapping("/{id}") 
    public NotificationResponse get(@PathVariable UUID id) {
        return service.get(id);
    }
    
    @GetMapping("/key/{key}") 
    public NotificationResponse getByKey(@PathVariable String key) {
        return service.getByKey(key);
    }
    
    @GetMapping("/user/{userId}") 
    public Page<NotificationResponse> byUser(@PathVariable String userId, Pageable pageable) {
        return service.byUser(userId, pageable);
    }
}
