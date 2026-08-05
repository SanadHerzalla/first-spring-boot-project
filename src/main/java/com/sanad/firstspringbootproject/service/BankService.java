package com.sanad.firstspringbootproject.service;

import com.sanad.firstspringbootproject.dto.BankResponse;
import com.sanad.firstspringbootproject.exception.BankNotFoundException;
import com.sanad.firstspringbootproject.exception.DuplicateBankException;
import com.sanad.firstspringbootproject.mapper.BankMapper;
import com.sanad.firstspringbootproject.model.Bank;
import com.sanad.firstspringbootproject.repository.SpringDataBankRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class BankService {

    private final SpringDataBankRepository bankRepository;
    private final BankMapper bankMapper;

    public BankService(SpringDataBankRepository bankRepository, BankMapper bankMapper) {
        this.bankRepository = bankRepository;
        this.bankMapper = bankMapper;
    }

    public List<BankResponse> findAll() {
        return bankRepository.findAll().stream().map(bankMapper::toResponse).toList();
    }

    public BankResponse findById(long id) {
        return bankMapper.toResponse(findEntityById(id));
    }

    @Transactional
    public BankResponse createBank(String requestedName) {
        String name = cleanName(requestedName);
        String normalizedName = normalizeName(name);

        if (bankRepository.existsByNormalizedName(normalizedName)) {
            throw new DuplicateBankException(name);
        }

        Bank bank = new Bank(name, normalizedName);

        try {
            Bank savedBank = bankRepository.saveAndFlush(bank);
            return bankMapper.toResponse(savedBank);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateBankException(name);
        }
    }

    @Transactional
    public BankResponse update(long id, String requestedName) {
        Bank bank = findEntityById(id);

        String name = cleanName(requestedName);
        String normalizedName = normalizeName(name);

        if (!bank.getNormalizedName().equals(normalizedName) && bankRepository.existsByNormalizedName(normalizedName)) {
            throw new DuplicateBankException(name);
        }

        bank.rename(name, normalizedName);

        try {
            Bank savedBank = bankRepository.saveAndFlush(bank);
            return bankMapper.toResponse(savedBank);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateBankException(name);
        }
    }

    @Transactional
    public void delete(Long id) {
        Bank bank = findEntityById(id);
        bankRepository.delete(bank);
    }

    private Bank findEntityById(Long id) {
        return bankRepository.findById(id).orElseThrow(() -> new BankNotFoundException(id));
    }

    private String cleanName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    private String normalizeName(String name) {
        return name.toLowerCase(Locale.ROOT);
    }
}