package com.sanad.firstspringbootproject.repository;

import com.sanad.firstspringbootproject.model.MoneyOperation;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class MoneyOperationProcedureRepository {

    private final EntityManager entityManager;

    public MoneyOperationProcedureRepository(
            EntityManager entityManager
    ) {
        this.entityManager = entityManager;
    }

    @Transactional
    public List<MoneyOperation> claimOperations(int batchSize) {

        return entityManager
                .createNativeQuery(
                        """
                        SELECT *
                        FROM claim_money_operations(:batchSize)
                        """,
                        MoneyOperation.class
                )
                .setParameter("batchSize", batchSize)
                .getResultList();
    }
}