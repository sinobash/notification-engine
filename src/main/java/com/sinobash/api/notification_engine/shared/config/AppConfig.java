package com.sinobash.api.notification_engine.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration 
@Import(KafkaConfig.class)
public class AppConfig {

}
