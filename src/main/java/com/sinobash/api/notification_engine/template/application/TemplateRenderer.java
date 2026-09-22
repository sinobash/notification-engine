package com.sinobash.api.notification_engine.template.application;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.sinobash.api.notification_engine.notification.domain.Notification;
import com.sinobash.api.notification_engine.template.infrastructure.NotificationTemplateRepository;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@Component 
public class TemplateRenderer {
    private static final Pattern TOKEN = Pattern.compile("\\{\\{\\s*([\\w.-]+)\\s*}}"); 
    private final NotificationTemplateRepository repo; 
    private final JsonMapper mapper;
    
    public TemplateRenderer(NotificationTemplateRepository repo, JsonMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }
    
    public String render(Notification n) throws Exception { 
        String body = n.getBody(); 
        if(n.getTemplateCode() != null) {
            var t = repo.findByCodeAndActiveTrue(n.getTemplateCode()).orElseThrow(() -> new IllegalArgumentException("Template not found: " + n.getTemplateCode())); 
            body = t.getBody();
        } 
        
        if(body == null) body=""; 
        Map<String,Object> params = n.getTemplateParams() == null ? Map.of() : mapper.readValue(n.getTemplateParams(), new TypeReference<>(){}); 
        Matcher m = TOKEN.matcher(body); 
        StringBuffer out = new StringBuffer(); 
        while(m.find()) {
            Object v = params.get(m.group(1)); 
            m.appendReplacement(out, Matcher.quoteReplacement(v == null ? m.group(0) : String.valueOf(v)));
        } 
        m.appendTail(out); 
        return out.toString(); 
    }
}
