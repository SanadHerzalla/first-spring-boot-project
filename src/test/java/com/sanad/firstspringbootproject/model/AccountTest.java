package com.sanad.firstspringbootproject.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void shouldCreateAccountWithZeroBalance() {
        Bank bank = new Bank("Arab Bank", "arab-bank");
        Account account = new Account("12345", "John Doe", AccountType.SAVINGS, bank);

        assertEquals("12345", account.getAccountNumber());
        assertEquals("John Doe", account.getOwnerName());
        assertEquals(AccountType.SAVINGS, account.getAccountType());
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertEquals(bank, account.getBank());
    }

    @Test
    void shouldUpdateAccountDetails() {
        Bank bank = new Bank("Arab Bank", "arab-bank");
        Account account = new Account("12345", "John Doe", AccountType.SAVINGS, bank);

        account.updateDetails("Jane Doe", AccountType.CURRENT);

        assertEquals("Jane Doe", account.getOwnerName());
        assertEquals(AccountType.CURRENT, account.getAccountType());
    }
}
