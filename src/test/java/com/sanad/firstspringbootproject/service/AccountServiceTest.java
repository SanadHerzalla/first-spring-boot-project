package com.sanad.firstspringbootproject.service;

import com.sanad.firstspringbootproject.dto.account.AccountResponse;
import com.sanad.firstspringbootproject.exception.AccountNotFoundException;
import com.sanad.firstspringbootproject.exception.DuplicateAccountException;
import com.sanad.firstspringbootproject.mapper.AccountMapper;
import com.sanad.firstspringbootproject.model.*;
import com.sanad.firstspringbootproject.repository.MoneyOperationRepository;
import com.sanad.firstspringbootproject.repository.SpringAccountRepository;
import com.sanad.firstspringbootproject.repository.SpringDataBankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private SpringAccountRepository accountRepository;

    @Mock
    private SpringDataBankRepository bankRepository;

    @Mock
    private MoneyOperationRepository moneyOperationRepository;

    @Mock
    private AccountMapper accountMapper;

    private AccountService accountService;

    @BeforeEach
    void setUp(){
        accountService  = new AccountService(accountRepository, bankRepository, moneyOperationRepository, accountMapper);
    }

    @Test
    void shouldFindAccountByAccountNumber(){
        Account account = mock(Account.class);

        AccountResponse expected = new AccountResponse(
                10L,
                "1111",
                "Sanad",
                AccountType.SAVINGS,
                new BigDecimal("500.00"),
                0L,
                1L
        );
        when(accountRepository.findByBankIdAndAccountNumber(1L, "1111")).thenReturn(Optional.of(account));

        when(accountMapper.toResponse(account)).thenReturn(expected);

        AccountResponse result = accountService.findByAccountNumber(1L, " 1111 ");

        assertEquals(expected, result);

        verify(accountRepository).findByBankIdAndAccountNumber(1L, "1111");
    }

    @Test
    void shouldThrowWhenAccountDoseNotExist(){
        when(accountRepository.findByBankIdAndAccountNumber(1L, "1111")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.findByAccountNumber(1L, " 1111"));
    }

    @Test
    void shouldCreateAccount(){
        Bank bank = mock(Bank.class);

        AccountResponse expected = new AccountResponse(
                10L,
                "1111",
                "Sanad",
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                0L,
                1L
        );

        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));

        when(accountRepository.existsByBankIdAndAccountNumber(1L, "1111")).thenReturn(false);

        when(accountRepository.saveAndFlush(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(accountMapper.toResponse(any(Account.class))).thenReturn(expected);

        AccountResponse result = accountService.createAccount(
                1L,
                " 1111 ",
                " Sanad ",
                AccountType.SAVINGS
        );

        assertEquals(expected, result);

        verify(accountRepository).saveAndFlush(any(Account.class));
    }

    @Test
    void shouldRejectDuplicateAccount(){
        Bank bank = mock(Bank.class);
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));

        when(accountRepository.existsByBankIdAndAccountNumber(1L, "1111")).thenReturn(true);

        assertThrows(DuplicateAccountException.class, () -> accountService.createAccount(
                1L,
                "1111",
                "Sanad",
                AccountType.SAVINGS
        ));

        verify(accountRepository,never()).saveAndFlush(any());
    }

    @Test
    void shouldUpdateAccount(){
        Account account = mock(Account.class);

        AccountResponse expected = new AccountResponse(
                10L,
                "1111",
                "Bader",
                AccountType.CURRENT,
                new BigDecimal("600.00"),
                1L,
                1L
        );

        when(accountRepository.findByBankIdAndAccountNumber(
                1L,
                "1111"
        )).thenReturn(Optional.of(account));

        when(accountRepository.saveAndFlush(account)).thenReturn(account);

        when(accountMapper.toResponse(account)).thenReturn(expected);

        AccountResponse result = accountService.updateAccount(
                1L,
                "1111",
                "          Bader   ",
                AccountType.CURRENT
        );
        assertEquals(expected, result);

        verify(account).updateDetails("Bader", AccountType.CURRENT);

        verify(accountRepository).saveAndFlush(account);
    }

    @Test
    void shouldDeleteAccount(){
        Account account = mock(Account.class);

        when(accountRepository.findByBankIdAndAccountNumber(
                1L,
                "1111"
        )).thenReturn(Optional.of(account));

        accountService.deleteAccount(1L, "1111");

        verify(accountRepository).delete(account);
    }

    @Test
    void shouldDepositSuccessfully(){
        BigDecimal amount = new BigDecimal("100.00");

        BigDecimal resultingBalance = new BigDecimal("600.00");

        Account account = mock(Account.class);
        MoneyOperation moneyOperation = mock(MoneyOperation.class);

        AccountResponse expected = new AccountResponse(
                10L,
                "1111",
                "Sanad",
                AccountType.SAVINGS,
                resultingBalance,
                1L,
                1L
        );

        when(moneyOperationRepository.claimOperation(
                "deposit-001",
                MoneyOperationType.DEPOSIT.name(),
                1L,
                "1111",
                null,
                null,
                amount
        )).thenReturn(1);

        when(accountRepository.deposit(1L, "1111", amount)).thenReturn(1);

        when(accountRepository.findByBankIdAndAccountNumber(1L, "1111")).thenReturn(Optional.of(account));

        when(account.getBalance()).thenReturn(resultingBalance);

        when(moneyOperationRepository.findByIdempotencyKey("deposit-001")).thenReturn(Optional.of(moneyOperation));

        when(accountMapper.toResponse(account)).thenReturn(expected);

        AccountResponse result = accountService.deposit(
                1L,
                "1111",
                amount,
                "deposit-001"
        );

        assertEquals(expected, result);

        verify(accountRepository).deposit(1L, "1111", amount);

        verify(moneyOperation).complete(resultingBalance);

    }
}
