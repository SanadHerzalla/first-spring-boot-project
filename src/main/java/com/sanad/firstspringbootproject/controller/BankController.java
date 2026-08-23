package com.sanad.firstspringbootproject.controller;

import com.sanad.firstspringbootproject.dto.bank.BankResponse;
import com.sanad.firstspringbootproject.dto.bank.CreateBankRequest;
import com.sanad.firstspringbootproject.dto.bank.UpdateBankRequest;
import com.sanad.firstspringbootproject.service.BankService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/v1/banks")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<BankResponse> getAllBanks() {
        return bankService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public BankResponse getBankById(@PathVariable long id) {
        return bankService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BankResponse> createBank(@Valid @RequestBody CreateBankRequest request) {
        BankResponse response = bankService.createBank(request.name());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BankResponse updateBank(@PathVariable long id, @Valid @RequestBody UpdateBankRequest request) {
        return bankService.update(id, request.name());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBank(@PathVariable long id) {
        bankService.delete(id);

        return ResponseEntity.noContent().build();
    }
}