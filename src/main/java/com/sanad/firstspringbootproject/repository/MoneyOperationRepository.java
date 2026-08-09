package com.sanad.firstspringbootproject.repository;

import com.sanad.firstspringbootproject.model.MoneyOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.Optional;

public interface MoneyOperationRepository extends JpaRepository<MoneyOperation, Long> {

    Optional<MoneyOperation> findByIdempotencyKey(String idempotencyKey);

    @Modifying
    @Query(value = """
            INSERT INTO money_operations (
                idempotency_key,
                operation_type,
                status,
                source_bank_id,
                source_account_number,
                destination_bank_id,
                destination_account_number,
                amount,
                created_at,
                version
            )
            VALUES (
                :idempotencyKey,
                :operationType,
                'PROCESSING',
                :sourceBankId,
                :sourceAccountNumber,
                :destinationBankId,
                :destinationAccountNumber,
                :amount,
                CURRENT_TIMESTAMP,
                0
            )
            ON CONFLICT (idempotency_key) DO NOTHING
            """, nativeQuery = true)
    int claimOperation(@Param("idempotencyKey") String idempotencyKey,

                       @Param("operationType") String operationType,

                       @Param("sourceBankId") Long sourceBankId,

                       @Param("sourceAccountNumber") String sourceAccountNumber,

                       @Param("destinationBankId") Long destinationBankId,

                       @Param("destinationAccountNumber")
                       String destinationAccountNumber,

                       @Param("amount") BigDecimal amount);

    String id(Long id);
}