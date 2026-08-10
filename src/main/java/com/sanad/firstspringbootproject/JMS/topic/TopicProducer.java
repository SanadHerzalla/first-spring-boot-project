package com.sanad.firstspringbootproject.JMS.topic;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class TopicProducer {
    private final JmsTemplate jmsTemplate;

    public TopicProducer(@Qualifier("topicJmsTemplate") JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(String message){
        jmsTemplate.convertAndSend("bank-topic",message);
    }
}
