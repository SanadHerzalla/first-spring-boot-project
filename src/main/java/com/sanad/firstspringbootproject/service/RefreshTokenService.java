package com.sanad.firstspringbootproject.service;

import com.sanad.firstspringbootproject.dto.auth.AuthResponse;
import com.sanad.firstspringbootproject.dto.auth.RefreshRequest;
import com.sanad.firstspringbootproject.model.RefreshToken;
import com.sanad.firstspringbootproject.model.User;
import com.sanad.firstspringbootproject.repository.RefreshTokenRepository;
import com.sanad.firstspringbootproject.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshExpiration;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshExpiration = refreshExpiration;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken(
                token,
                user,
                Instant.now().plusMillis(refreshExpiration),
                false
        );

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow( () ->
                new IllegalArgumentException("Invalid refresh token")
        );

        if (refreshToken.isRevoked()){
            throw new IllegalStateException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())){
            throw new IllegalStateException("Refresh token has expired");
        }
        return refreshToken;
    }
}
