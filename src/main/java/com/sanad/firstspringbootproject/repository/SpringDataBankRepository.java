package com.sanad.firstspringbootproject.repository;

import com.sanad.firstspringbootproject.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataBankRepository extends JpaRepository<Bank, Long> {
    boolean existsByNormalizedName(String normalizedName);
}
