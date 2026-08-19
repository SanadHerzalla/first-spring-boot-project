package com.sanad.firstspringbootproject.service;

import com.sanad.firstspringbootproject.dto.auth.AuthResponse;
import com.sanad.firstspringbootproject.dto.auth.LoginRequest;
import com.sanad.firstspringbootproject.dto.auth.RegisterRequest;
import com.sanad.firstspringbootproject.dto.auth.RegisterResponse;
import com.sanad.firstspringbootproject.exception.DuplicateUserException;
import com.sanad.firstspringbootproject.model.Role;
import com.sanad.firstspringbootproject.repository.UserRepository;
import com.sanad.firstspringbootproject.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sanad.firstspringbootproject.model.User;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request){
        if (userRepository.existsByUsername(request.username())){
            throw new DuplicateUserException("Username already exists");
        }

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                Role.USER
        );

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getUsername(),
                savedUser.getRole().name()
        );
    }

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        User user = userRepository.findByUsername(request.username()).orElseThrow();

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name()
        );
    }

}
