package com.sanad.firstspringbootproject.JMS.queue;
import com.sanad.firstspringbootproject.JMS.job.StatementJob;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class StatementProducer {
    private final JmsTemplate jmsTemplate;

    public StatementProducer(@Qualifier("queueJmsTemplate") JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void requestStatement(Long bankId, String accountNumber) {
        StatementJob statementJob = new StatementJob(bankId, accountNumber);
        jmsTemplate.convertAndSend("statement-queue", statementJob);
    }
}
