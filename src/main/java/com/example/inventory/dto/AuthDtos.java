package com.example.inventory.dto;

import com.example.inventory.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() { }

    public record RegisterRequest(
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Size(min = 3, max = 60) String username,
            @NotBlank @Email @Size(max = 160) String email,
            @NotBlank @Size(min = 8, max = 100) String password,
            @NotNull @Positive Long branchId) { }

    public record LoginRequest(@NotBlank String usernameOrEmail, @NotBlank String password) { }

    public record AuthResponse(String token, String tokenType, long expiresInMs, UserResponse user) { }

    public record UserResponse(Long id, String name, String username, String email, Role role, Long branchId) { }

    public record AdminCreateUserRequest(
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Size(min = 3, max = 60) String username,
            @NotBlank @Email @Size(max = 160) String email,
            @NotBlank @Size(min = 8, max = 100) String password,
            @NotNull Role role,
            @Positive Long branchId) { }
}
