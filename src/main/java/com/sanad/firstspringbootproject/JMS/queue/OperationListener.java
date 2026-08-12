package com.sanad.firstspringbootproject.JMS.queue;

import com.sanad.firstspringbootproject.JMS.job.OperationJob;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class OperationListener {
    @JmsListener(destination = "operation-queue", containerFactory = "queueFactory")
    public void processOperation(OperationJob job) {
        System.out.println(
                "Processing operation: "
                + job.operationId()
                + " | "
                + job.operationType()
                + " | Account: "
                + job.accountNumber()
                + " | Amount: "
                + job.amount()

        );
    }
}
