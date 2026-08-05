package com.sanad.firstspringbootproject.controller;


import com.sanad.firstspringbootproject.dto.account.AccountResponse;
import com.sanad.firstspringbootproject.dto.account.CreateAccountRequest;
import com.sanad.firstspringbootproject.dto.account.UpdateAccountRequest;
import com.sanad.firstspringbootproject.model.Account;
import com.sanad.firstspringbootproject.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banks/{bankId}/accounts")
public class AccountController {
    private final AccountService accountService;

    public  AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResponse> getAllAccounts(@PathVariable Long bankId) {
        return accountService.findAllByBankId(bankId);
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse getAccount(@PathVariable long bankId, @PathVariable String accountNumber) {
        return accountService.findByAccountNumber(bankId, accountNumber);
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@PathVariable long bankId, @Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(bankId, request.accountNumber(), request.ownerName(), request.accountType());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{accountNumber}")
    public AccountResponse updateAccount(@PathVariable long bankId, @PathVariable String accountNumber, @Valid @RequestBody UpdateAccountRequest request) {
        return accountService.updateAccount(
                bankId,
                accountNumber,
                request.ownerName(),
                request.accountType()
        );
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(@PathVariable long bankId, @PathVariable String accountNumber) {
        accountService.deleteAccount(bankId, accountNumber);
        return ResponseEntity.noContent().build();
    }
}
