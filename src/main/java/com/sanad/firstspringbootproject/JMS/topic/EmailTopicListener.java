package com.sanad.firstspringbootproject.JMS.topic;


import jakarta.jms.Topic;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class EmailTopicListener {

    @JmsListener(destination = "bank-topic", containerFactory = "topicFactory")
    public void receiveMessage(String message) {
        System.out.println("Sending email notification: " + message);
    }
}
