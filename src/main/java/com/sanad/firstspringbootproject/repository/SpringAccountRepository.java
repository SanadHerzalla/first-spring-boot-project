package com.sanad.firstspringbootproject.repository;

import com.sanad.firstspringbootproject.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.Optional;

public interface SpringAccountRepository extends JpaRepository<Account, Long> {
    Page<Account> findAllByBankId(Long bankId, Pageable pageable);

    Optional<Account> findByBankIdAndAccountNumber(Long bankId, String accountNumber);

    boolean existsByBankIdAndAccountNumber(Long bankId, String accountNumber);

    boolean existsByBankId(Long bankId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Account account
            SET account.balance = account.balance + :amount,
                account.version = account.version + 1
            WHERE account.bank.id = :bankId
            AND account.accountNumber = :accountNumber
            """)
    int deposit(@Param("bankId") Long bankId, @Param("accountNumber") String accountNumber, @Param("amount") BigDecimal amount);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Account account
            SET account.balance = account.balance - :amount,
                account.version = account.version + 1
            WHERE account.bank.id = :bankId
            AND account.accountNumber = :accountNumber
            AND account.balance >= :amount
            """)
    int withdraw(@Param("bankId") Long bankId, @Param("accountNumber") String accountNumber, @Param("amount") BigDecimal amount);
}
