package com.sanad.firstspringbootproject.dto.auth;

import jakarta.validation.constraints.NotBlank;
import org.aspectj.weaver.ast.Not;

public record LoginRequest(
        @NotBlank
        String username,

        @NotBlank
        String password
) {
}
