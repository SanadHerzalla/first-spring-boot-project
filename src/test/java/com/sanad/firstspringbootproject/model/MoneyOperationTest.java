package com.sanad.firstspringbootproject.model;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class MoneyOperationTest {
    @Test
    void shouldPrintOperationTime(){
        MoneyOperation moneyOperation = new MoneyOperation(
                "test",
                MoneyOperationType.DEPOSIT,
                1L,
                "1111",
                null,
                null,
                new BigDecimal(100.00)
        );

        moneyOperation.onCreate();

        System.out.println("Created at: "+ moneyOperation.getCreatedAt());
        System.out.println("Updated at: "+ moneyOperation.getUpdatedAt());

        moneyOperation.onUpdate();
        System.out.println("Updated at: "+ moneyOperation.getUpdatedAt());
    }
}
