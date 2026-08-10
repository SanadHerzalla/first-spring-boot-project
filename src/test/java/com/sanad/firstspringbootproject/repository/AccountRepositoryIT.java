package com.sanad.firstspringbootproject.repository;

import com.sanad.firstspringbootproject.model.Account;
import com.sanad.firstspringbootproject.model.AccountType;
import com.sanad.firstspringbootproject.model.Bank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AccountRepositoryIT {

    @Autowired
    private SpringAccountRepository accountRepository;

    @Autowired
    private SpringDataBankRepository bankRepository;

    @Test
    void shouldFindAccountByBankIdAndAccountNumber(){
        Bank bank = new Bank("Arab bank","arab bank");
        bank = bankRepository.saveAndFlush(bank);

        Account account = new Account("1111", "Sanad",AccountType.SAVINGS, bank);

        accountRepository.saveAndFlush(account);

        Optional<Account> result = accountRepository.findByBankIdAndAccountNumber(bank.getId(),"1111");
        assertTrue(result.isPresent());

        assertEquals("1111", result.get().getAccountNumber());

        assertEquals("Sanad", result.get().getOwnerName());
    }

    @Test
    void shouldDepositMoneyIntoAccount(){
        Bank bank = new Bank("Arab Bank", "arab bank");
        bank = bankRepository.saveAndFlush(bank);

        Account account = new  Account("1111", "Sanad",AccountType.SAVINGS, bank);

        accountRepository.saveAndFlush(account);

        BigDecimal amount = new BigDecimal("100");

        int updatedRows = accountRepository.deposit(
                bank.getId(),
                "1111",
                amount
        );
        assertEquals(1, updatedRows);

        Account udpatedAccount = accountRepository.findByBankIdAndAccountNumber(bank.getId(),"1111").orElseThrow();

        assertEquals(0, udpatedAccount.getBalance().compareTo(new  BigDecimal("100")));
    }

    @Test
    void shouldWithdrawMoneyWhenBalanceIsEnough(){
        Bank bank = new Bank("Arab Bank", "arab bank");
        bank = bankRepository.saveAndFlush(bank);
        Account account = new Account("1111", "Sanad",AccountType.SAVINGS, bank);
        accountRepository.saveAndFlush(account);

        accountRepository.deposit(
                bank.getId(),
                "1111",
                new BigDecimal("500.00")
        );

        int updatedRows = accountRepository.withdraw(
                bank.getId(),
                "1111",
                new BigDecimal("100.00")
        );
        assertEquals(1, updatedRows);

        Account updatedAccount =  accountRepository.findByBankIdAndAccountNumber(bank.getId(),"1111").orElseThrow();

        assertEquals(0, updatedAccount.getBalance().compareTo(new  BigDecimal("400.00")));
    }

    @Test
    void shouldNotWithdrawMoneyWhenBalanceIsInsufficient(){
        Bank bank = new Bank("Arab Bank", "arab bank");
        bank = bankRepository.saveAndFlush(bank);

        Account account = new  Account("1111", "Sanad",AccountType.SAVINGS, bank);
        accountRepository.saveAndFlush(account);

        accountRepository.deposit(
                bank.getId(),
                "1111",
                new BigDecimal("10.00")
        );

        int updatedRows = accountRepository.withdraw(
                bank.getId(),
                "1111",
                new BigDecimal("100.00")
        );
        assertEquals(0, updatedRows);

        Account updatedAccount = accountRepository.findByBankIdAndAccountNumber(bank.getId(),"1111").orElseThrow();

        assertEquals(0, updatedAccount.getBalance().compareTo(new  BigDecimal("10.00")));
    }

    @Test
    void shouldReturnZeroWhenDepositingIntoMissingAccount(){
        int updatedRows = accountRepository.deposit(
                90L,
                "1298489021",
                new BigDecimal("100.00")
        );
        assertEquals(0, updatedRows);
    }

    @Test
    void shouldReturnZeroWhenWithdrawingIntoMissingAccount(){
        int updatedRows = accountRepository.withdraw(
                90L,
                "1298489021",
                new BigDecimal("100.00")
        );
        assertEquals(0, updatedRows);
    }
}
