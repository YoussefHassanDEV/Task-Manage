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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository users;
    @Mock PasswordEncoder encoder;
    @Mock JwtUtils jwtUtils;

    @InjectMocks AuthService service;

    @Test
    void register_savesUser_andReturnsExpected() {
        when(encoder.encode("pw")).thenReturn("hash");
        when(users.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtUtils.generateAccessToken("e@example.com")).thenReturn("tok");

        String result = service.register(new RegisterRequest("e@example.com", "pw" ));

        assertEquals("tok", result);
        verify(users, times(1)).save(any(User.class));
        verify(encoder).encode("pw");
        verify(jwtUtils).generateAccessToken("e@example.com");
    }

    @Test
    void login_valid_returnsExpected() {
        var u = User.builder().id(1L).email("e@example.com").passwordHash("hash").build();
        when(users.findByEmail("e@example.com")).thenReturn(Optional.of(u));
        when(encoder.matches("pw", "hash")).thenReturn(true);
        when(jwtUtils.generateAccessToken("e@example.com")).thenReturn("tok");
        when(jwtUtils.getAccessExpirationMillis()).thenReturn(900_000L);
        when(jwtUtils.generateRefreshToken("e@example.com")).thenReturn("rtok");
        when(jwtUtils.getRefreshExpirationMillis()).thenReturn(604_800_000L);

        LoginResponse result = service.login(new LoginRequest("e@example.com", "pw"));

        assertEquals("tok", result.accessToken());
        assertEquals("rtok", result.refreshToken());
        assertEquals(900_000L, result.expiresInMillis());
        assertEquals(604_800_000L, result.refreshExpiresInMillis());
        verify(users).findByEmail("e@example.com");
        verify(encoder).matches("pw", "hash");
        verify(jwtUtils).generateAccessToken("e@example.com");
        verify(jwtUtils).generateRefreshToken("e@example.com");
    }

    @Test
    void login_invalidPassword_throws() {
        var u = User.builder().id(1L).email("e@example.com").passwordHash("hash").build();
        when(users.findByEmail("e@example.com")).thenReturn(Optional.of(u));
        when(encoder.matches("bad", "hash")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> service.login(new LoginRequest("e@example.com", "bad")));

        verify(users).findByEmail("e@example.com");
        verify(encoder).matches("bad", "hash");
        verify(jwtUtils, never()).generateAccessToken(any());
        verify(jwtUtils, never()).generateRefreshToken(any());
    }
}
