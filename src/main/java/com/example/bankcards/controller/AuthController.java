package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "API для аутентификации")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                         JwtTokenProvider tokenProvider,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(AuthResponse.builder()
                .token(jwt)
                .username(user.getUsername())
                .role(user.getRole())
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody CreateUserRequest createUserRequest) {
        if (userRepository.existsByUsername(createUserRequest.getUsername())) {
            throw new RuntimeException("Username already exists: " + createUserRequest.getUsername());
        }

        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + createUserRequest.getEmail());
        }

        User user = User.builder()
                .username(createUserRequest.getUsername())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .email(createUserRequest.getEmail())
                .role(createUserRequest.getRole() != null ? createUserRequest.getRole() : Role.USER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        String jwt = tokenProvider.generateTokenFromUsername(savedUser.getUsername());

        return ResponseEntity.ok(AuthResponse.builder()
                .token(jwt)
                .username(savedUser.getUsername())
                .role(savedUser.getRole())
                .build());
    }
}
