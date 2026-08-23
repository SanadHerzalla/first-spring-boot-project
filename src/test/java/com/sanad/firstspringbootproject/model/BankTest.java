package com.sanad.firstspringbootproject.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    @Test
    void shouldCreateBank() {
        Bank bank = new Bank("Arab Bank", "arab-bank");

        assertEquals("Arab Bank", bank.getName());
        assertEquals("arab-bank", bank.getNormalizedName());
    }

    @Test
    void shouldRenameBank() {
        Bank bank = new Bank("Arab Bank", "arab-bank");

        bank.rename("Cairo Amman Bank", "cairo-amman-bank");

        assertEquals("Cairo Amman Bank", bank.getName());
        assertEquals("cairo-amman-bank", bank.getNormalizedName());
    }
}
