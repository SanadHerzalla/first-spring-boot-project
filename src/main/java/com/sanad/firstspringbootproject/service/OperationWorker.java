package com.sanad.firstspringbootproject.service;


import com.sanad.firstspringbootproject.JMS.job.OperationJob;
import com.sanad.firstspringbootproject.JMS.queue.OperationProducer;
import com.sanad.firstspringbootproject.model.MoneyOperation;
import com.sanad.firstspringbootproject.repository.MoneyOperationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OperationWorker {
    private final MoneyOperationRepository moneyOperationRepository;
    private final OperationProducer operationProducer;

    public  OperationWorker(MoneyOperationRepository moneyOperationRepository, OperationProducer operationProducer) {
        this.moneyOperationRepository = moneyOperationRepository;
        this.operationProducer = operationProducer;
    }

    @Async("operationExecutor")
    public void process(Long operationId){
        System.out.println(
                Thread.currentThread().getName() + " processing operations " + operationId
        );
        MoneyOperation operation = moneyOperationRepository.findById(operationId).orElseThrow();

        OperationJob operationJob = new OperationJob(
                operation.getId(),
                operation.getOperationType().name(),
                operation.getSourceBankId(),
                operation.getSourceAccountNumber(),
                operation.getAmount()
        );

        try {
            operationProducer.send(operationJob);

            operation.markPublished();

            moneyOperationRepository.save(operation);
        } catch (Exception exception) {
            operation.markPending();
            moneyOperationRepository.save(operation);
        }

    }
}
