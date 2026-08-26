package com.sanad.firstspringbootproject.dto.auth;

public record AuthResponse(
        String token,
        String refreshToken,
        String username,
        String role
){
}
