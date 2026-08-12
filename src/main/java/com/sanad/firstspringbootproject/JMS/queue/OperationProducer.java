package com.sanad.firstspringbootproject.JMS.queue;

import com.sanad.firstspringbootproject.JMS.job.OperationJob;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class OperationProducer {
    private final JmsTemplate queueJmsTemplate;
    public OperationProducer(@Qualifier("queueJmsTemplate") JmsTemplate queueJmsTemplate) {
        this.queueJmsTemplate = queueJmsTemplate;
    }

    public void send(OperationJob operationJob) {
        System.out.println("Sending operation: " + operationJob.operationId());
        queueJmsTemplate.convertAndSend("operation-queue" ,operationJob);

    }
}

