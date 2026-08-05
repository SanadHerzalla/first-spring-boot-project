package com.sanad.firstspringbootproject.mapper;

import com.sanad.firstspringbootproject.dto.bank.BankResponse;
import com.sanad.firstspringbootproject.model.Bank;
import org.springframework.stereotype.Component;

@Component
public class BankMapper {
    public BankResponse toResponse(Bank bank) {
        return new  BankResponse(bank.getId(), bank.getName(), bank.getVersion());
    }
}
