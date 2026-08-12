package com.sanad.firstspringbootproject.JMS.job;

import com.sanad.firstspringbootproject.JMS.queue.OperationProducer;
import com.sanad.firstspringbootproject.model.MoneyOperation;
import com.sanad.firstspringbootproject.repository.MoneyOperationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperationLogJob {
    private final MoneyOperationRepository moneyOperationRepository;
    private final OperationProducer operationProducer;
    public OperationLogJob(MoneyOperationRepository moneyOperationRepository,  OperationProducer operationProducer) {
        this.moneyOperationRepository = moneyOperationRepository;
        this.operationProducer = operationProducer;
    }

    @Scheduled(fixedDelay = 10000, fixedRate = 10000)
    public void publishOperations() {
        System.out.println("Operation job started");
        List<MoneyOperation> operations = moneyOperationRepository.findTop10ByPublishedFalseOrderByIdAsc();
        System.out.println("Found " + operations.size() + " unpublished operations");

        for (MoneyOperation operation : operations) {
            System.out.println("Preparing operation ID: " + operation.getId());

            OperationJob job = new OperationJob(
                    operation.getId(),
                    operation.getOperationType().name(),
                    operation.getSourceBankId(),
                    operation.getSourceAccountNumber(),
                    operation.getAmount()
            );
            operationProducer.send(job);
            operation.markPublished();
            moneyOperationRepository.save(operation);
        }
    }
}
