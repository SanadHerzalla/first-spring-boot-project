package com.sanad.firstspringbootproject.JMS.queue;

import com.sanad.firstspringbootproject.JMS.job.StatementJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class StatementListener {

    @JmsListener(destination = "statement-queue")
    public void generateStatement(StatementJob statementJob) {
        System.out.println("Generating statement for bank: "
                + statementJob.bankId()
                + ", account: "
                + statementJob.accountNumber()
        );
    }
}
