package com.sanad.firstspringbootproject.service;


import com.sanad.firstspringbootproject.JMS.queue.OperationProducer;
import com.sanad.firstspringbootproject.model.MoneyOperation;
import com.sanad.firstspringbootproject.repository.MoneyOperationRepository;
import jakarta.transaction.Transactional;
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
    @Transactional
    public void process(MoneyOperation operation){
        System.out.println(
                Thread.currentThread().getName() + " processing operations " + operation.getId()
        );

        try {
            System.out.println("Operation is processed");
            operation.markPublished();
            moneyOperationRepository.save(operation);
        } catch (Exception exception) {
            operation.markPending();
            moneyOperationRepository.save(operation);
        }

    }
}
