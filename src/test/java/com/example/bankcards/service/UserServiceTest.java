package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private CreateUserRequest createUserRequest;
    private User user;
    
    @BeforeEach
    void setUp() {
        createUserRequest = CreateUserRequest.builder()
            .username("testuser")
            .password("password123")
            .email("test@example.com")
            .role(Role.USER)
            .build();
        
        user = User.builder()
            .id(1L)
            .username("testuser")
            .password("encodedPassword")
            .email("test@example.com")
            .role(Role.USER)
            .enabled(true)
            .build();
    }
    
    @Test
    void createUser_Success() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // When
        UserDto result = userService.createUser(createUserRequest);
        
        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());
        assertTrue(result.isEnabled());
        
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void createUser_UsernameAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.createUser(createUserRequest);
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.createUser(createUserRequest);
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
}
