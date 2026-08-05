package com.sanad.firstspringbootproject.dto.account;

import com.sanad.firstspringbootproject.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateAccountRequest(
        @NotBlank(message = "Owner name is required")
        @Size(
                min = 2,
                max = 100,
                message = "Owner name musts be between 2 and 100 characters"
        )
        String ownerName,

        @NotNull(message = "Account type is required")
        AccountType accountType
) {

}
