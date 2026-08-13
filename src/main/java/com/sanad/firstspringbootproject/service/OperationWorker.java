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

    public OperationWorker(MoneyOperationRepository moneyOperationRepository) {
        this.moneyOperationRepository = moneyOperationRepository;
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
