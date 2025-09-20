package com.example.Task.Manage.DTOs.Response;

public record LoginResponse(
        String accessToken,
        long expiresInMillis,
        String refreshToken,
        long refreshExpiresInMillis
) {}
