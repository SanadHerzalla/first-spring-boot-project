package com.sanad.firstspringbootproject.dto.bank;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// record automatically provides (constructor, accessor methods, equals(), hashcode(), toString())
public record CreateBankRequest(

        @NotBlank(message = "Bank name is required")
        @Size(
                min = 2,
                max = 100,
                message = "Bank name must be between 2 and 100 characters"
        )
        String name

) {
}