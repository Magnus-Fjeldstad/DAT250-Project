package com.example.pollapp.dto;

public record RegisterRequestDto(
        String username,
        String email,
        String password
) {}
