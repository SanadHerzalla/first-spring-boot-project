package com.sanad.firstspringbootproject.JMS.job;

import com.sanad.firstspringbootproject.JMS.queue.OperationProducer;
import com.sanad.firstspringbootproject.model.MoneyOperation;
import com.sanad.firstspringbootproject.repository.MoneyOperationRepository;
import com.sanad.firstspringbootproject.service.OperationClaimService;
import com.sanad.firstspringbootproject.service.OperationWorker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperationLogJob {
    private final OperationClaimService  operationClaimService;
    private final OperationWorker worker;
    public OperationLogJob(OperationClaimService operationClaimService , OperationWorker worker) {
        this.operationClaimService = operationClaimService;
        this.worker = worker;
    }

    @Scheduled(fixedDelay = 1000)
    public void publishOperations() {
        List<Long> operations = operationClaimService.claimOperations(10);

        System.out.println("Claimed: " + operations.size() + " operations");

        for (Long operationId : operations) {
            worker.process(operationId);
        }
    }
}
