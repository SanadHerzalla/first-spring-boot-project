package com.sanad.firstspringbootproject.JMS.job;

import com.sanad.firstspringbootproject.model.MoneyOperation;
import com.sanad.firstspringbootproject.repository.MoneyOperationRepository;
import com.sanad.firstspringbootproject.service.OperationWorker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperationLogJob {
    private final MoneyOperationRepository operationRepository;
    private final OperationWorker worker;
    public OperationLogJob(MoneyOperationRepository operationRepository , OperationWorker worker) {
        this.operationRepository = operationRepository;
        this.worker = worker;
    }

    @Scheduled(fixedDelay = 1000)
    public void publishOperations() {
        List<MoneyOperation> operations = operationRepository.claimOperations(10);

        System.out.println("Claimed: " + operations.size() + " operations");

        for (MoneyOperation operation : operations) {
            worker.process(operation);
        }
    }
}
