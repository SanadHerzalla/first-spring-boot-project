package com.sanad.firstspringbootproject.JMS.job;

import com.sanad.firstspringbootproject.model.MoneyOperation;
import com.sanad.firstspringbootproject.repository.MoneyOperationProcedureRepository;
import com.sanad.firstspringbootproject.repository.MoneyOperationRepository;
import com.sanad.firstspringbootproject.service.OperationWorker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperationLogJob {
    private final MoneyOperationProcedureRepository moneyOperationProcedureRepository;
    private final OperationWorker worker;
    public OperationLogJob(MoneyOperationProcedureRepository moneyOperationProcedureRepository, OperationWorker worker) {
        this.moneyOperationProcedureRepository = moneyOperationProcedureRepository;
        this.worker = worker;
    }

    @Scheduled(fixedDelay = 1000)
    public void publishOperations() {
        List<MoneyOperation> operations = moneyOperationProcedureRepository.claimOperations(10);

        System.out.println("Claimed: " + operations.size() + " operations");

        for (MoneyOperation operation : operations) {
            worker.process(operation);
        }
    }
}
