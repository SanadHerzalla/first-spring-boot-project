package com.sanad.firstspringbootproject.repository;

import com.sanad.firstspringbootproject.model.Bank;
import tools.jackson.databind.introspect.DefaultAccessorNamingStrategy;

import java.util.List;
import java.util.Optional;

public interface BankRepository {
    List<Bank> findAll();

    Optional<Bank> findById(long id);

    boolean existsByNormalizedName(String normalizedName);

    Bank saveAndFlush(Bank bank);

    void delete(Bank bank);
}
