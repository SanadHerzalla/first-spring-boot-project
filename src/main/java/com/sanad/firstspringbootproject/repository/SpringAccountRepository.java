package com.sanad.firstspringbootproject.repository;

import com.sanad.firstspringbootproject.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringAccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByBankId(Long bankId);

    Optional<Account> findByBankIdAndAccountNumber(Long bankId, String accountNumber);

    boolean existsByBankIdAndAccountNumber(Long bankId, String accountNumber);
}
