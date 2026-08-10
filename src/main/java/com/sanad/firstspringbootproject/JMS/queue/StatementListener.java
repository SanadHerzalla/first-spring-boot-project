package com.sanad.firstspringbootproject.JMS.queue;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class StatementListener {
    @JmsListener(destination = "statement-queue")
    public void generateStatement(Long accountNumber) {
        System.out.println("Generating statement for account: " + accountNumber);
    }
}
