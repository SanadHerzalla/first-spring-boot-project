package com.sanad.firstspringbootproject.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

@Configuration
public class JmsConfig {
    @Bean
    public JmsTemplate topicJmsTemplate(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

    @Bean
    public JmsTemplate queueJmsTemplate(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(false);
        return jmsTemplate;
    }

    @Bean
    public DefaultJmsListenerContainerFactory topicFactory(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }
}
