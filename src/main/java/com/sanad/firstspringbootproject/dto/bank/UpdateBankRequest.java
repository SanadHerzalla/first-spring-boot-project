package com.sanad.firstspringbootproject.dto.bank;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBankRequest(

        @NotBlank(message = "Bank name is required")
        @Size(
                min = 2,
                max = 100,
                message = "Bank name must be between 2 and 100 characters"
        )
        String name

) {
}