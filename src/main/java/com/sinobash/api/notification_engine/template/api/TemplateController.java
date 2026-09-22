package com.sinobash.api.notification_engine.template.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sinobash.api.notification_engine.notification.domain.NotificationChannel;
import com.sinobash.api.notification_engine.template.domain.NotificationTemplate;
import com.sinobash.api.notification_engine.template.infrastructure.NotificationTemplateRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController 
@RequestMapping("/api/v1/templates") 
public class TemplateController {
    private final NotificationTemplateRepository repo; 

    public TemplateController(NotificationTemplateRepository repo) {
        this.repo = repo;
    }

    public record Request(@NotBlank String code, @NotNull NotificationChannel channel, @NotBlank String subject, @NotBlank String body){}

    @GetMapping 
    public List<NotificationTemplate> list() {
        return repo.findAll();
    }

    @PostMapping 
    public ResponseEntity<NotificationTemplate> create(@Valid @RequestBody Request r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(
            new NotificationTemplate(
                r.code(), 
                r.channel(), 
                r.subject(), 
                r.body()
            )
        ));
    }
}
