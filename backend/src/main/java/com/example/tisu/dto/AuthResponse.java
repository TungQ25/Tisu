package com.example.tisu.dto;

public record AuthResponse(
        String id,
        String username,
        String email,
        long createdAt,
        String token,
        String tokenType,
        long expiresAt
) {
}