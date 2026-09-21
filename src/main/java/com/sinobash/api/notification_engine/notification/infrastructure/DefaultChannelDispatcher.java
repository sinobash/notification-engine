package com.sinobash.api.notification_engine.notification.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.sinobash.api.notification_engine.notification.domain.NotificationChannel;

@Component 
public class DefaultChannelDispatcher implements ChannelDispatcher {
    private static final Logger log = LoggerFactory.getLogger(DefaultChannelDispatcher.class);
    private final JavaMailSender mail; 
    private final boolean mailEnabled;
    
    public DefaultChannelDispatcher(JavaMailSender mail, @Value("${notification.delivery.email-enabled:false}") boolean mailEnabled) {
        this.mail = mail;
        this.mailEnabled = mailEnabled;
    }

    @Override
    public void dispatch(NotificationChannel channel, String recipient, String body) {
        switch(channel){
            case EMAIL -> {
                if(!mailEnabled) { 
                    log.info("Email delivery (log adapter): recipient={}, body={}", recipient,body); 
                    return; 
                }
                var m = new SimpleMailMessage(); 
                m.setTo(recipient); 
                m.setSubject("Notification"); 
                m.setText(body); 
                mail.send(m);
            }
            case SMS -> log.info("SMS delivery (adapter): recipient={}, body={}", recipient,body);
            case PUSH -> log.info("Push delivery (adapter): recipient={}, body={}", recipient,body);
        }
    }

}
