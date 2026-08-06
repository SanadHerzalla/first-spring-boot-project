package com.sanad.firstspringbootproject.service;

import com.sanad.firstspringbootproject.dto.bank.BankResponse;
import com.sanad.firstspringbootproject.exception.BankNotFoundException;
import com.sanad.firstspringbootproject.exception.DuplicateBankException;
import com.sanad.firstspringbootproject.mapper.BankMapper;
import com.sanad.firstspringbootproject.model.Bank;
import com.sanad.firstspringbootproject.repository.SpringDataBankRepository;
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
    private BankMapper bankMapper;

    @InjectMocks
    private BankService bankService;

    @Test
    void findAllShouldReturnMappedBankResponses() {
        Bank firstBank = new Bank("Arab Bank", "arab bank");
        Bank secondBank = new Bank("Housing Bank", "housing bank");

        BankResponse firstResponse = new BankResponse(1L, "Arab Bank", 0L);

        BankResponse secondResponse = new BankResponse(2L, "Housing Bank", 0L);

        when(bankRepository.findAll()).thenReturn(List.of(firstBank, secondBank));

        when(bankMapper.toResponse(firstBank)).thenReturn(firstResponse);

        when(bankMapper.toResponse(secondBank)).thenReturn(secondResponse);

        List<BankResponse> result = bankService.findAll();

        assertEquals(2, result.size());
        assertEquals(firstResponse, result.get(0));
        assertEquals(secondResponse, result.get(1));

        verify(bankRepository).findAll();
        verify(bankMapper).toResponse(firstBank);
        verify(bankMapper).toResponse(secondBank);
    }

    @Test
    void findByIdShouldReturnBankResponseWhenBankExists() {
        long bankId = 1L;

        Bank bank = new Bank("Arab Bank", "arab bank");
        BankResponse expectedResponse = new BankResponse(bankId, "Arab Bank", 0L);

        when(bankRepository.findById(bankId)).thenReturn(Optional.of(bank));

        when(bankMapper.toResponse(bank)).thenReturn(expectedResponse);

        BankResponse result = bankService.findById(bankId);

        assertEquals(expectedResponse, result);

        verify(bankRepository).findById(bankId);
        verify(bankMapper).toResponse(bank);
    }

    @Test
    void findByIdShouldThrowExceptionWhenBankDoesNotExist() {
        long bankId = 99L;

        when(bankRepository.findById(bankId)).thenReturn(Optional.empty());

        BankNotFoundException exception = assertThrows(BankNotFoundException.class, () -> bankService.findById(bankId));

        assertEquals("Bank with ID " + bankId + " was not found", exception.getMessage());

        verify(bankRepository).findById(bankId);
        verifyNoInteractions(bankMapper);
    }

    @Test
    void createBankShouldSaveAndReturnResponse() {
        String requestedName = "  Arab   Bank  ";

        Bank savedBank = new Bank("Arab Bank", "arab bank");

        BankResponse expectedResponse = new BankResponse(1L, "Arab Bank", 0L);

        when(bankRepository.existsByNormalizedName("arab bank")).thenReturn(false);

        when(bankRepository.saveAndFlush(any(Bank.class))).thenReturn(savedBank);

        when(bankMapper.toResponse(savedBank)).thenReturn(expectedResponse);

        BankResponse result = bankService.createBank(requestedName);

        assertEquals(expectedResponse, result);

        ArgumentCaptor<Bank> bankCaptor = ArgumentCaptor.forClass(Bank.class);

        verify(bankRepository).saveAndFlush(bankCaptor.capture());

        Bank bankPassedToRepository = bankCaptor.getValue();

        assertEquals("Arab Bank", bankPassedToRepository.getName());
        assertEquals("arab bank", bankPassedToRepository.getNormalizedName());

        verify(bankRepository).existsByNormalizedName("arab bank");

        verify(bankMapper).toResponse(savedBank);
    }

    @Test
    void createBankShouldThrowExceptionWhenNameAlreadyExists() {
        String requestedName = "Arab Bank";

        when(bankRepository.existsByNormalizedName("arab bank")).thenReturn(true);

        DuplicateBankException exception = assertThrows(DuplicateBankException.class, () -> bankService.createBank(requestedName));

        assertEquals("A bank named 'Arab Bank' already exists", exception.getMessage());

        verify(bankRepository).existsByNormalizedName("arab bank");

        verify(bankRepository, never()).saveAndFlush(any(Bank.class));

        verifyNoInteractions(bankMapper);
    }

    @Test
    void createBankShouldTranslateDatabaseConstraintViolation() {
        String requestedName = "Arab Bank";

        when(bankRepository.existsByNormalizedName("arab bank")).thenReturn(false);

        when(bankRepository.saveAndFlush(any(Bank.class))).thenThrow(new DataIntegrityViolationException("Unique constraint violated"));

        DuplicateBankException exception = assertThrows(DuplicateBankException.class, () -> bankService.createBank(requestedName));

        assertEquals("A bank named 'Arab Bank' already exists", exception.getMessage());

        verify(bankRepository).existsByNormalizedName("arab bank");

        verify(bankRepository).saveAndFlush(any(Bank.class));

        verifyNoInteractions(bankMapper);
    }

    @Test
    void updateShouldRenameAndReturnUpdatedBank() {
        long bankId = 1L;

        Bank existingBank = new Bank("Arab Bank", "arab bank");

        BankResponse expectedResponse = new BankResponse(bankId, "Arab Banking Corporation", 1L);

        when(bankRepository.findById(bankId)).thenReturn(Optional.of(existingBank));

        when(bankRepository.existsByNormalizedName("arab banking corporation")).thenReturn(false);

        when(bankRepository.saveAndFlush(existingBank)).thenReturn(existingBank);

        when(bankMapper.toResponse(existingBank)).thenReturn(expectedResponse);

        BankResponse result = bankService.update(bankId, "  Arab   Banking   Corporation  ");

        assertEquals(expectedResponse, result);

        assertEquals("Arab Banking Corporation", existingBank.getName());

        assertEquals("arab banking corporation", existingBank.getNormalizedName());

        verify(bankRepository).findById(bankId);

        verify(bankRepository).existsByNormalizedName("arab banking corporation");

        verify(bankRepository).saveAndFlush(existingBank);

        verify(bankMapper).toResponse(existingBank);
    }

    @Test
    void updateShouldThrowExceptionWhenAnotherBankHasName() {
        long bankId = 1L;

        Bank existingBank = new Bank("Arab Bank", "arab bank");

        when(bankRepository.findById(bankId)).thenReturn(Optional.of(existingBank));

        when(bankRepository.existsByNormalizedName("housing bank")).thenReturn(true);

        DuplicateBankException exception = assertThrows(DuplicateBankException.class, () -> bankService.update(bankId, "Housing Bank"));

        assertEquals("A bank named 'Housing Bank' already exists", exception.getMessage());

        assertEquals("Arab Bank", existingBank.getName());
        assertEquals("arab bank", existingBank.getNormalizedName());

        verify(bankRepository).findById(bankId);

        verify(bankRepository).existsByNormalizedName("housing bank");

        verify(bankRepository, never()).saveAndFlush(any(Bank.class));

        verifyNoInteractions(bankMapper);
    }

    @Test
    void updateShouldAllowBankToKeepItsCurrentName() {
        long bankId = 1L;

        Bank existingBank = new Bank("Arab Bank", "arab bank");

        BankResponse expectedResponse = new BankResponse(bankId, "Arab Bank", 0L);

        when(bankRepository.findById(bankId)).thenReturn(Optional.of(existingBank));

        when(bankRepository.saveAndFlush(existingBank)).thenReturn(existingBank);

        when(bankMapper.toResponse(existingBank)).thenReturn(expectedResponse);

        BankResponse result = bankService.update(bankId, "Arab Bank");

        assertEquals(expectedResponse, result);

        verify(bankRepository).findById(bankId);

        verify(bankRepository, never()).existsByNormalizedName(anyString());

        verify(bankRepository).saveAndFlush(existingBank);

        verify(bankMapper).toResponse(existingBank);
    }

    @Test
    void deleteShouldDeleteBankWhenItExists() {
        long bankId = 1L;

        Bank bank = new Bank("Arab Bank", "arab bank");

        when(bankRepository.findById(bankId)).thenReturn(Optional.of(bank));

        bankService.delete(bankId);

        verify(bankRepository).findById(bankId);
        verify(bankRepository).delete(bank);
    }

    @Test
    void deleteShouldThrowExceptionWhenBankDoesNotExist() {
        long bankId = 99L;

        when(bankRepository.findById(bankId)).thenReturn(Optional.empty());

        BankNotFoundException exception = assertThrows(BankNotFoundException.class, () -> bankService.delete(bankId));

        assertEquals("Bank with ID " + bankId + " was not found", exception.getMessage());

        verify(bankRepository).findById(bankId);
        verify(bankRepository, never()).delete(any(Bank.class));
    }
}