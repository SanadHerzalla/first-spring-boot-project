package com.sanad.firstspringbootproject.model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "money_operations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_money_operations_idempotency_key",
                        columnNames = "idempotency_key"
                )
        }
)
public class MoneyOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "idempotency_key",
            nullable = false,
            length = 100
    )
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "operation_type",
            nullable = false,
            length = 30
    )
    private MoneyOperationType operationType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private MoneyOperationStatus status;

    @Column(name = "source_bank_id", nullable = false,  length = 50)
    private Long sourceBankId;

    @Column(name = "source_account_number", nullable = false, length = 50)
    private String sourceAccountNumber;

    @Column(name = "destination_bank_id")
    private Long destinationBankId;

    @Column(name = "destination_account_number", length = 50)
    private String destinationAccountNumber;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Column(name = "resulting_balance", precision = 19, scale = 2)
    private BigDecimal resultingBalance;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublishStatus publishStatus = PublishStatus.PENDING;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    protected MoneyOperation() {}

    public MoneyOperation(
            String idempotencyKey,
            MoneyOperationType operationType,
            Long sourceBankId,
            String sourceAccountNumber,
            Long destinationBankId,
            String destinationAccountNumber,
            BigDecimal amount
    ) {
        this.idempotencyKey = idempotencyKey;
        this.operationType = operationType;
        this.sourceBankId = sourceBankId;
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationBankId = destinationBankId;
        this.destinationAccountNumber = destinationAccountNumber;
        this.amount = amount;
        this.status = MoneyOperationStatus.PROCESSING;
        this.createdAt = OffsetDateTime.now();
    }

    public void complete(BigDecimal resultingBalance) {
        this.resultingBalance = resultingBalance;
        this.status = MoneyOperationStatus.COMPLETED;
        this.completedAt = OffsetDateTime.now();
    }

    public void fail(){
        this.status = MoneyOperationStatus.FAILED;
        this.completedAt = OffsetDateTime.now();
    }

    public boolean matches(
            MoneyOperationType operationType,
            Long sourceBankId,
            String sourceAccountNumber,
            Long destinationBankId,
            String destinationAccountNumber,
            BigDecimal amount
            ){
        return this.operationType == operationType
                && this.sourceBankId.equals(sourceBankId)
                && this.sourceAccountNumber.equals(sourceAccountNumber)
                && java.util.Objects.equals(this.destinationBankId, destinationBankId)
                && java.util.Objects.equals(this.destinationAccountNumber, destinationAccountNumber)
                && this.amount.compareTo(amount) == 0;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public MoneyOperationStatus getStatus() {
        return status;
    }

    public BigDecimal getResultingBalance() {
        return resultingBalance;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getId() {
        return id;
    }

    public void markPublished() {
        this.publishStatus = PublishStatus.PUBLISHED;
    }

    public void markProcessing() {
        this.publishStatus = PublishStatus.PROCESSING;
    }

    public void markPending() {
        this.publishStatus = PublishStatus.PENDING;
    }

    public MoneyOperationType getOperationType() {
        return operationType;
    }

    public Long getSourceBankId() {
        return sourceBankId;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public Long getDestinationBankId() {
        return destinationBankId;
    }

    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public Long getVersion() {
        return version;
    }
}
