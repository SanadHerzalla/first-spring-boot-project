package com.sanad.firstspringbootproject.JMS.queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class StatementProducer {
    private final JmsTemplate jmsTemplate;

    public StatementProducer(@Qualifier("queueJmsTemplate") JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void requestProducer(Long accountNumber) {
        jmsTemplate.convertAndSend("statement-queue", accountNumber);
    }
}
