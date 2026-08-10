package com.sanad.firstspringbootproject.service;

import com.sanad.firstspringbootproject.dto.bank.BankResponse;
import com.sanad.firstspringbootproject.exception.BankNotFoundException;
import com.sanad.firstspringbootproject.exception.DuplicateBankException;
import com.sanad.firstspringbootproject.mapper.BankMapper;
import com.sanad.firstspringbootproject.model.Bank;
import com.sanad.firstspringbootproject.repository.SpringAccountRepository;
import com.sanad.firstspringbootproject.repository.SpringDataBankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock
    private SpringDataBankRepository bankRepository;

    @Mock
    private SpringAccountRepository accountRepository;

    @Mock
    private BankMapper bankMapper;

    private BankService bankService;

    @BeforeEach
    void setUp() {
        bankService = new BankService(bankRepository, accountRepository, bankMapper);
    }

    @Test
    void shouldFindBankById() {
        Bank bank = new Bank("Arab Bank", "arab bank");

        BankResponse expectedResponse = new BankResponse(1L, "Arab Bank", 0L);
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));
        when(bankMapper.toResponse(bank)).thenReturn(expectedResponse);

        BankResponse result = bankService.findById(1L);
        assertEquals(expectedResponse, result);

        verify(bankRepository).findById(1L);

        verify(bankMapper).toResponse(bank);
    }

    @Test
    void shouldThrowBankNotFoundWhenBankDoseNotExist() {
        when(bankRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BankNotFoundException.class, () -> bankService.findById(1L));

        verify(bankRepository).findById(1L);
        verifyNoMoreInteractions(bankMapper);
    }

    @Test
    void shouldReturnAllBanks() {
        Bank bank1 = new Bank("Arab bank", "arab bank");
        Bank bank2 = new Bank("Housing Bank", "housing bank");

        BankResponse response1 = new BankResponse(1L, "Arab Bank", 0L);
        BankResponse response2 = new BankResponse(2L, "Housing Bank", 0L);

        when(bankRepository.findAll()).thenReturn(List.of(bank1, bank2));
        when(bankMapper.toResponse(bank1)).thenReturn(response1);
        when(bankMapper.toResponse(bank2)).thenReturn(response2);

        List<BankResponse> result = bankService.findAll();
        assertEquals(2, result.size());

        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(bankRepository).findAll();
        verify(bankMapper).toResponse(bank1);
        verify(bankMapper).toResponse(bank2);
    }

    @Test
    void shouldCreateBank() {

        String bankName = "Jordan Bank";
        String normalizedName = "jordan bank";

        BankResponse expectedResponse =
                new BankResponse(
                        1L,
                        bankName,
                        0L
                );

        when(
                bankRepository.existsByNormalizedName(
                        normalizedName
                )
        ).thenReturn(false);

        when(bankRepository.saveAndFlush(any(Bank.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        when(bankMapper.toResponse(any(Bank.class)))
                .thenReturn(expectedResponse);

        BankResponse result =
                bankService.createBank(bankName);

        assertEquals(expectedResponse, result);

        verify(bankRepository)
                .existsByNormalizedName(normalizedName);

        verify(bankRepository)
                .saveAndFlush(any(Bank.class));

        verify(bankMapper)
                .toResponse(any(Bank.class));
    }

}