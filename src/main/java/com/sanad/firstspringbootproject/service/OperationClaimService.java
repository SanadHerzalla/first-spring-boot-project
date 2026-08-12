package com.sanad.firstspringbootproject.service;

import com.sanad.firstspringbootproject.model.MoneyOperation;
import com.sanad.firstspringbootproject.repository.MoneyOperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OperationClaimService {
    private final MoneyOperationRepository operationRepository;

    public OperationClaimService(MoneyOperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Transactional
    public List<Long> claimOperations(int batchSize){
        List<MoneyOperation> operations = operationRepository.findOperationsToClaim(batchSize);

        for (MoneyOperation operation : operations) {
            operation.markProcessing();
        }
        operationRepository.flush();

        return operations.stream().map(MoneyOperation::getId).toList();
    }
}
