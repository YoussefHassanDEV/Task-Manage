package com.example.Task.Manage.service;

import com.example.Task.Manage.DTOs.Request.LoginRequest;
import com.example.Task.Manage.DTOs.Request.RegisterRequest;
import com.example.Task.Manage.DTOs.Response.LoginResponse;
import com.example.Task.Manage.model.User;
import com.example.Task.Manage.repository.UserRepository;

import com.example.Task.Manage.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository users;
    @Mock PasswordEncoder encoder;
    @Mock
    JwtUtils jwtUtils;

    @InjectMocks AuthService service;

    @Test
    void register_savesUser_andReturnsExpected() {
        // Arrange
        when(encoder.encode("pw")).thenReturn("hash");
        when(users.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtUtils.generateToken("e@example.com")).thenReturn("tok");

        // Act
        // If AuthService.register returns a String token:
        String result = service.register(new RegisterRequest("e@example.com", "pw"));

        // Assert
        assertEquals("tok", result);
        verify(users, times(1)).save(any(User.class));
        verify(encoder).encode("pw");
        verify(jwtUtils).generateToken("e@example.com");
    }

    @Test
    void login_valid_returnsExpected() {
        // Arrange
        var u = User.builder().id(1L).email("e@example.com").passwordHash("hash").build();
        when(users.findByEmail("e@example.com")).thenReturn(Optional.of(u));
        when(encoder.matches("pw", "hash")).thenReturn(true);
        when(jwtUtils.generateToken("e@example.com")).thenReturn("tok");

        // Act
        // If AuthService.login returns a String token:
        LoginResponse result = service.login(new LoginRequest("e@example.com", "pw"));

        // Assert
        assertEquals("tok", result.accessToken());
        verify(users).findByEmail("e@example.com");
        verify(encoder).matches("pw", "hash");
        verify(jwtUtils).generateToken("e@example.com");
    }

    @Test
    void login_invalidPassword_throws() {
        // Arrange
        var u = User.builder().id(1L).email("e@example.com").passwordHash("hash").build();
        when(users.findByEmail("e@example.com")).thenReturn(Optional.of(u));
        when(encoder.matches("bad", "hash")).thenReturn(false);

        // Act + Assert
        assertThrows(BadCredentialsException.class, () -> service.login(new LoginRequest("e@example.com", "bad")));


        verify(users).findByEmail("e@example.com");
        verify(encoder).matches("bad", "hash");
        verify(jwtUtils, never()).generateToken(any());
    }
}
