package com.example.Task.Manage.service;

import com.example.Task.Manage.DTOs.Request.LoginRequest;
import com.example.Task.Manage.DTOs.Request.RegisterRequest;
import com.example.Task.Manage.DTOs.Response.LoginResponse;
import com.example.Task.Manage.model.User; // IMPORTANT: domain entity, not org.apache.catalina.User
import com.example.Task.Manage.repository.UserRepository;
import com.example.Task.Manage.security.JwtUtils;
import com.example.Task.Manage.security.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService blacklistService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       TokenBlacklistService blacklistService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.blacklistService = blacklistService;
    }

    public String register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User u = User.builder()
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .build();
        userRepository.save(u);
        return jwtUtils.generateToken(req.email());
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String token = jwtUtils.generateToken(user.getEmail());
        return new LoginResponse(token, jwtUtils.getExpirationMillis());
    }

    public void logout(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) return;
        String token = bearerToken.substring(7);
        try {
            Jws<Claims> jws = jwtUtils.validateAndParse(token);
            Date exp = jws.getBody().getExpiration();
            blacklistService.blacklist(token, exp.getTime());
        } catch (Exception ignored) { }
    }
}
