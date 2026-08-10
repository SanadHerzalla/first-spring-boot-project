package com.sanad.firstspringbootproject.JMS.topic;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class BankTopicIT {

    @Autowired
    private TopicProducer topicProducer;

    @MockitoSpyBean
    private EmailTopicListener emailTopicListener;

    @MockitoSpyBean
    private SmsTopicListener smsTopicListener;

    @Test
    void sendMessageToAllTopicsSubscribers(){
        String message = "Transfer Completed";

        topicProducer.sendMessage(message);

        verify(emailTopicListener, timeout(3000)).receiveMessage(message);

        verify(emailTopicListener, timeout(3000)).receiveMessage(message);
    }
}
