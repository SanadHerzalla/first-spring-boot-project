package com.sanad.firstspringbootproject.JMS.topic;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class SmsTopicListener {

    @JmsListener(destination = "bank-topic", containerFactory = "topicFactory")
    public void receiveMessage(String message) {
        System.out.println("Sending Sms notification: " + message);
    }
}
