package com.sanad.firstspringbootproject.service;

import com.sanad.firstspringbootproject.dto.account.AccountResponse;
import com.sanad.firstspringbootproject.dto.account.TransferResponse;
import com.sanad.firstspringbootproject.exception.*;
import com.sanad.firstspringbootproject.mapper.AccountMapper;
import com.sanad.firstspringbootproject.model.*;
import com.sanad.firstspringbootproject.repository.MoneyOperationRepository;
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

    private final MoneyOperationRepository moneyOperationRepository;
    private final SpringAccountRepository accountRepository;
    private final SpringDataBankRepository bankRepository;
    private final AccountMapper accountMapper;

    public AccountService(
            SpringAccountRepository accountRepository,
            SpringDataBankRepository bankRepository,
            MoneyOperationRepository moneyOperationRepository,
            AccountMapper accountMapper
    ) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
        this.accountMapper = accountMapper;
        this.moneyOperationRepository = moneyOperationRepository;
    }

    public PageResponse<AccountResponse> findAllByBankId(long bankId, int page, int size) {
        findBankById(bankId);

        validatePagintation(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Account> accountPage = accountRepository.findAllByBankId(bankId, pageable);

        List<AccountResponse> content = accountPage.getContent().stream().map(accountMapper::toResponse).toList();

        return new PageResponse<>(
                content,
                accountPage.getNumber(),
                accountPage.getSize(),
                accountPage.getTotalElements(),
                accountPage.getTotalPages(),
                accountPage.isFirst(),
                accountPage.isLast());
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
    public AccountResponse deposit(long bankId, String requestedAccountNumber, BigDecimal amount, String requestedIdempotencyKey) {
        validateAmount(amount);
        String accountNumber = cleanAccountNumber(requestedAccountNumber);
        String idempotencyKey = cleanIdempotencyKey(requestedIdempotencyKey);

        int claimed = moneyOperationRepository. claimOperation(
                idempotencyKey,
                MoneyOperationType.DEPOSIT.name(),
                bankId,
                accountNumber,
                null,
                null,
                amount
        );

        if (claimed == 0) {
            return handleRepeatedOperation(
                    idempotencyKey,
                    MoneyOperationType.DEPOSIT,
                    bankId,
                    accountNumber,
                    amount
            );
        }

        int updatedRows = accountRepository.deposit(bankId, accountNumber, amount);

        if (updatedRows == 0) {
            throw new AccountNotFoundException(bankId, accountNumber);
        }

        Account updatedAccount = findAccount(bankId, accountNumber);

        MoneyOperation operation = findMoneyOperation(idempotencyKey);

        operation.complete(updatedAccount.getBalance());

        return accountMapper.toResponse(updatedAccount);
    }

    @Transactional
    public AccountResponse withdraw(
            long bankId,
            String requestedAccountNumber,
            BigDecimal amount,
            String requestedIdempotencyKey
    ) {
        validateAmount(amount);
        String accountNumber = cleanAccountNumber(requestedAccountNumber);
        String idempotencyKey = cleanIdempotencyKey(requestedIdempotencyKey);

        int claimed = moneyOperationRepository.claimOperation(
                idempotencyKey,
                MoneyOperationType.WITHDRAW.name(),
                bankId,
                accountNumber,
                null,
                null,
                amount
        );

        if (claimed == 0) {
            return handleRepeatedOperation(
                    idempotencyKey,
                    MoneyOperationType.WITHDRAW,
                    bankId,
                    accountNumber,
                    amount
            );
        }

        int updatedRows = accountRepository.withdraw(bankId, accountNumber, amount);
        if (updatedRows == 0) {
            Account account = findAccount(bankId, accountNumber);

            throw new InsufficientBalanceException(accountNumber, account.getBalance());
        }

        Account updatedAccount = findAccount(bankId, accountNumber);

        MoneyOperation operation = findMoneyOperation(idempotencyKey);

        operation.complete(updatedAccount.getBalance());

        return accountMapper.toResponse(updatedAccount);
    }

    @Transactional
    public TransferResponse transfer(
            long sourceBankId,
            String requestedSourceAccountNumber,
            long destinationBankId,
            String requestedDestinationAccountNumber,
            BigDecimal amount,
            String requestedIdempotencyKey
    ) {
        validateAmount(amount);


        String sourceAccountNumber = cleanAccountNumber(requestedSourceAccountNumber);
        String destinationAccountNumber = cleanAccountNumber(requestedDestinationAccountNumber);
        String idempotencyKey = cleanIdempotencyKey(requestedIdempotencyKey);

        validateDifferentAccounts(sourceBankId, sourceAccountNumber, destinationBankId, destinationAccountNumber);
        findAccount(destinationBankId, destinationAccountNumber);

        int claimed = moneyOperationRepository.claimOperation(
                idempotencyKey,
                MoneyOperationType.TRANSFER.name(),
                sourceBankId,
                sourceAccountNumber,
                destinationBankId,
                destinationAccountNumber,
                amount
        );

        if (claimed == 0) {
            return handleRepeatedTransfer(
                    idempotencyKey,
                    sourceBankId,
                    sourceAccountNumber,
                    destinationBankId,
                    destinationAccountNumber,
                    amount
            );
        }

        int withdrawnRows = accountRepository.withdraw(sourceBankId, sourceAccountNumber, amount);

        if (withdrawnRows == 0) {
            Account sourceAccount = findAccount(sourceBankId, sourceAccountNumber);
            throw new InsufficientBalanceException(sourceAccountNumber, sourceAccount.getBalance());
        }

        int depositRows = accountRepository.deposit(destinationBankId, destinationAccountNumber, amount);

        if (depositRows == 0) {
            throw new AccountNotFoundException(destinationBankId, destinationAccountNumber);
        }

        Account updateSource = findAccount(sourceBankId, sourceAccountNumber);

        Account updateDestination = findAccount(destinationBankId, destinationAccountNumber);

        MoneyOperation operation = findMoneyOperation(idempotencyKey);

        operation.complete(updateSource.getBalance());

        return new TransferResponse(
                accountMapper.toResponse(updateSource),
                accountMapper.toResponse(updateDestination),
                amount
        );

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
        if (page < 0) {
            throw new IllegalArgumentException("Page number must not be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }
    }

    private void validateDifferentAccounts(long sourceBankId, String sourceAccountNumber, long destinationBankId, String destinationAccountNumber) {
        boolean sameBank = sourceBankId == destinationBankId;

        boolean sameAccount = sourceAccountNumber.equals(destinationAccountNumber);

        if (sameBank && sameAccount) {
            throw new SameAccountTransferException();
        }
    }

    private TransferResponse handleRepeatedTransfer(
            String idempotencyKey,
            long sourceBankId,
            String sourceAccountNumber,
            long destinationBankId,
            String destinationAccountNumber,
            BigDecimal amount
    ) {
        MoneyOperation operation = findMoneyOperation(idempotencyKey);

        boolean sameRequest = operation.matches(
                MoneyOperationType.TRANSFER,
                sourceBankId,
                sourceAccountNumber,
                destinationBankId,
                destinationAccountNumber,
                amount
        );

        if (!sameRequest) {
            throw new IdempotencyConflictException(idempotencyKey);
        }

        if (operation.getStatus() == MoneyOperationStatus.PROCESSING) {
            throw new OperationInProgressException(idempotencyKey);
        }

        Account source = findAccount(sourceBankId, sourceAccountNumber);
        Account destination = findAccount(destinationBankId, destinationAccountNumber);
        AccountResponse sourceResponse = accountMapper.toResponse(source);

        sourceResponse = new AccountResponse(
                sourceResponse.id(),
                sourceResponse.accountNumber(),
                sourceResponse.ownerName(),
                sourceResponse.accountType(),
                operation.getResultingBalance(),
                sourceResponse.version(),
                sourceResponse.bankId()
        );

        return new TransferResponse(
                sourceResponse,
                accountMapper.toResponse(destination),
                amount
        );
    }

    private AccountResponse handleRepeatedOperation(
            String idempotencyKey,
            MoneyOperationType operationType,
            long bankId,
            String accountNumber,
            BigDecimal amount
    ) {
        MoneyOperation operation = findMoneyOperation(idempotencyKey);

        boolean sameRequest = operation.matches(operationType, bankId, accountNumber, null, null, amount);

        if (!sameRequest) {
            throw new IdempotencyConflictException(idempotencyKey);
        }

        if (operation.getStatus() == MoneyOperationStatus.PROCESSING) {
            throw new OperationInProgressException(idempotencyKey);
        }

        Account account = findAccount(bankId, accountNumber);

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getOwnerName(),
                account.getAccountType(),
                operation.getResultingBalance(),
                account.getVersion(),
                account.getBank().getId()
        );
    }

    private MoneyOperation findMoneyOperation(String idempotencyKey) {
        return moneyOperationRepository.findByIdempotencyKey(idempotencyKey).orElseThrow(() -> new IllegalStateException("Money operation not found"));
    }

    private String cleanIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            throw new IllegalArgumentException("idempotency-Key header is required");
        }

        String cleanedKey = idempotencyKey.trim();

        if (cleanedKey.length() > 100) {
            throw new IllegalArgumentException("Idempotency-Key must not exceed 100 characters");
        }
        return cleanedKey;
    }
}