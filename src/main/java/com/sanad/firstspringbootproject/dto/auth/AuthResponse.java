package com.sanad.firstspringbootproject.dto.auth;

public record AuthResponse(
        String token,
        String username,
        String role
){
}
