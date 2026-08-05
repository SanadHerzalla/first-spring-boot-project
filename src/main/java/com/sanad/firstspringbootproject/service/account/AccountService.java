package com.sanad.firstspringbootproject.service;

import com.sanad.firstspringbootproject.dto.account.AccountResponse;
import com.sanad.firstspringbootproject.exception.account.AccountNotFoundException;
import com.sanad.firstspringbootproject.exception.bank.BankNotFoundException;
import com.sanad.firstspringbootproject.exception.account.DuplicateAccountException;
import com.sanad.firstspringbootproject.mapper.AccountMapper;
import com.sanad.firstspringbootproject.model.Account;
import com.sanad.firstspringbootproject.model.AccountType;
import com.sanad.firstspringbootproject.model.Bank;
import com.sanad.firstspringbootproject.repository.SpringAccountRepository;
import com.sanad.firstspringbootproject.repository.SpringDataBankRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<AccountResponse> findAllByBankId(long bankId) {
        findBankById(bankId);

        return accountRepository.findAllByBankId(bankId).stream().map(accountMapper::toResponse).toList();
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
}