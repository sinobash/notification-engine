package com.sinobash.api.notification_engine.shared.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import com.sinobash.api.notification_engine.notification.infrastructure.NotificationEvent;

@Configuration
public class KafkaConfig {

    @Bean 
    NewTopic highTopic() {
        return TopicBuilder.name("notifications.high").partitions(6).replicas(1).build();
    }

    @Bean
    NewTopic lowTopic() {
        return TopicBuilder.name("notifications.low").partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic dlqTopic() {
        return TopicBuilder.name("notifications.dlq").partitions(3).replicas(1).build();
    }

    @Bean
    ProducerFactory<String, NotificationEvent> producerFactory(KafkaProperties p) {
        return new DefaultKafkaProducerFactory<>(p.buildProducerProperties());
    }

    @Bean
    KafkaTemplate<String, NotificationEvent> kafkaTemplate(ProducerFactory<String, NotificationEvent> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean ConsumerFactory<String, NotificationEvent> consumerFactory(KafkaProperties p) {
        Map<String,Object> props = new HashMap<>(p.buildConsumerProperties());
        JacksonJsonDeserializer<NotificationEvent> jd = new JacksonJsonDeserializer<>(NotificationEvent.class); 
        jd.trustedPackages("com.sinobash.api.notification_engine.notification.infrastructure");
    
        return new DefaultKafkaConsumerFactory<>(props,new StringDeserializer(),jd);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> kafkaListenerContainerFactory(ConsumerFactory<String, NotificationEvent> cf) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, NotificationEvent>(); 
        f.setConsumerFactory(cf); 
        return f;
    }

}