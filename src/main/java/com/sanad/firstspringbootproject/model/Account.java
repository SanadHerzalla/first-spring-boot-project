package com.sanad.firstspringbootproject.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_accounts_bank_account_number",
                columnNames = {
                        "bank_id",
                        "account_number"
                }
        )
})
public class Account {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "account_number",
            nullable = false,
            length = 50
    )
    private String accountNumber;

    @Column(
            name = "owner_name",
            nullable = false,
            length = 100
    )
    private String ownerName;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "account_type",
            nullable = false,
            length = 30
    )
    private AccountType accountType;

    @Column(
            name = "balance",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal balance;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "bank_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_accounts_bank"
            )
    )
    private Bank bank;

    protected Account() {}

    public Account(String accountNumber, String ownerName, AccountType accountType, Bank bank) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.accountType = accountType;
        this.balance = BigDecimal.ZERO;
        this.bank = bank;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Long getVersion() {
        return version;
    }

    public Bank getBank() {
        return bank;
    }

    public void updateDetails(String ownerName, AccountType accountType) {
        this.ownerName = ownerName;
        this.accountType = accountType;
    }
}
