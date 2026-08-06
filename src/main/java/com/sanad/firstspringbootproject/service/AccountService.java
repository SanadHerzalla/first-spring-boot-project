package com.sanad.firstspringbootproject.service;

import com.sanad.firstspringbootproject.dto.account.AccountResponse;
import com.sanad.firstspringbootproject.exception.*;
import com.sanad.firstspringbootproject.mapper.AccountMapper;
import com.sanad.firstspringbootproject.model.Account;
import com.sanad.firstspringbootproject.model.AccountType;
import com.sanad.firstspringbootproject.model.Bank;
import com.sanad.firstspringbootproject.repository.SpringAccountRepository;
import com.sanad.firstspringbootproject.repository.SpringDataBankRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sanad.firstspringbootproject.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AccountService {

    private final SpringAccountRepository accountRepository;
    private final SpringDataBankRepository bankRepository;
    private final AccountMapper accountMapper;

    public AccountService(SpringAccountRepository accountRepository, SpringDataBankRepository bankRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
        this.accountMapper = accountMapper;
    }

    public PageResponse<AccountResponse> findAllByBankId(long bankId, int page, int size) {
        findBankById(bankId);

        validatePagintation(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Account> accountPage = accountRepository.findAllByBankId(bankId, pageable);

        List<AccountResponse> content = accountPage.getContent().stream().map(accountMapper::toResponse).toList();

        return new PageResponse<>(content, accountPage.getNumber(), accountPage.getSize(), accountPage.getTotalElements(), accountPage.getTotalPages()
        , accountPage.isFirst(), accountPage.isLast());
    }

    public AccountResponse findByAccountNumber(long bankId, String requestedAccountNumber) {
        String accountNumber = cleanAccountNumber(requestedAccountNumber);

        Account account = findAccount(bankId, accountNumber);

        return accountMapper.toResponse(account);
    }

    @Transactional
    public AccountResponse createAccount(long bankId, String requestedAccountNumber, String requestedOwnerName, AccountType accountType) {
        Bank bank = findBankById(bankId);

        String accountNumber = cleanAccountNumber(requestedAccountNumber);

        String ownerName = cleanOwnerName(requestedOwnerName);

        if (accountRepository.existsByBankIdAndAccountNumber(bankId, accountNumber)) {
            throw new DuplicateAccountException(accountNumber, bankId);
        }

        Account account = new Account(accountNumber, ownerName, accountType, bank);

        try {
            Account savedAccount = accountRepository.saveAndFlush(account);

            return accountMapper.toResponse(savedAccount);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateAccountException(accountNumber, bankId);
        }
    }

    @Transactional
    public AccountResponse updateAccount(long bankId, String requestedAccountNumber, String requestedOwnerName, AccountType accountType) {
        String accountNumber = cleanAccountNumber(requestedAccountNumber);

        String ownerName = cleanOwnerName(requestedOwnerName);

        Account account = findAccount(bankId, accountNumber);

        account.updateDetails(ownerName, accountType);

        Account savedAccount = accountRepository.saveAndFlush(account);

        return accountMapper.toResponse(savedAccount);
    }

    @Transactional
    public void deleteAccount(long bankId, String requestedAccountNumber) {
        String accountNumber = cleanAccountNumber(requestedAccountNumber);

        Account account = findAccount(bankId, accountNumber);

        accountRepository.delete(account);
    }

    @Transactional
    public AccountResponse deposit(long bankId, String requestedAccountNumber, BigDecimal amount) {
        validateAmount(amount);
        String accountNumber = cleanAccountNumber(requestedAccountNumber);

        int updatedRows = accountRepository.deposit(bankId, accountNumber, amount);

        if (updatedRows == 0){
            throw new AccountNotFoundException(bankId, accountNumber);
        }

        Account updatedAccount = findAccount(bankId, accountNumber);

        return accountMapper.toResponse(updatedAccount);
    }

    @Transactional
    public AccountResponse withdraw(long bankId, String requestedAccountNumber, BigDecimal amount) {
        validateAmount(amount);
        String accountNumber = cleanAccountNumber(requestedAccountNumber);

        int updatedRows = accountRepository.withdraw(bankId, accountNumber, amount);
        if (updatedRows == 0){
            Account account = findAccount(bankId, accountNumber);
            throw new InsufficientBalanceException(accountNumber, account.getBalance());
        }

        Account updatedAccount = findAccount(bankId, accountNumber);

        return  accountMapper.toResponse(updatedAccount);
    }

    private Bank findBankById(long bankId) {
        return bankRepository.findById(bankId).orElseThrow(() -> new BankNotFoundException(bankId));
    }

    private Account findAccount(long bankId, String accountNumber) {
        return accountRepository.findByBankIdAndAccountNumber(bankId, accountNumber).orElseThrow(() -> new AccountNotFoundException(bankId, accountNumber));
    }

    private String cleanAccountNumber(String accountNumber) {
        return accountNumber.trim();
    }

    private String cleanOwnerName(String ownerName) {
        return ownerName.trim().replaceAll("\\s+", " ");
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(amount);
        }
    }

    private void validatePagintation(int page, int size) {
        if (page < 0){
            throw new IllegalArgumentException("Page number must not be negative");
        }

        if (size < 1 || size > 100){
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }
    }
}