package com.sanad.firstspringbootproject.controller;


import com.sanad.firstspringbootproject.dto.MoneyOperationRequest;
import com.sanad.firstspringbootproject.dto.PageResponse;
import com.sanad.firstspringbootproject.dto.account.AccountResponse;
import com.sanad.firstspringbootproject.dto.account.CreateAccountRequest;
import com.sanad.firstspringbootproject.dto.account.UpdateAccountRequest;
import com.sanad.firstspringbootproject.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/banks/{bankId}/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public PageResponse<AccountResponse> getAllAccounts(@PathVariable Long bankId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return accountService.findAllByBankId(bankId, page, size);
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
        return accountService.updateAccount(bankId, accountNumber, request.ownerName(), request.accountType());
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(@PathVariable long bankId, @PathVariable String accountNumber) {
        accountService.deleteAccount(bankId, accountNumber);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{accountNumber}/deposit")
    public AccountResponse deposit(@PathVariable long bankId, @PathVariable String accountNumber, @Valid @RequestBody MoneyOperationRequest request) {
        return accountService.deposit(bankId, accountNumber, request.amount());
    }

    @PostMapping("/{accountNumber}/withdraw")
    public AccountResponse withdraw(@PathVariable long bankId, @PathVariable String accountNumber, @Valid @RequestBody MoneyOperationRequest request) {
        return accountService.withdraw(bankId, accountNumber, request.amount());
    }
}
