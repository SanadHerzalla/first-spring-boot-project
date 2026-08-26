package com.sanad.firstspringbootproject.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(

        @NotBlank String refreshToken
) {

}
