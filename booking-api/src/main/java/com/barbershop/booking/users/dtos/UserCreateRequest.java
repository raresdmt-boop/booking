package com.barbershop.booking.users.dtos;

import com.barbershop.booking.users.models.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "UserCreateRequest", description = "Data required to create a user")
public record UserCreateRequest(

        @Schema(description = "User email", example = "test@gmail.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @Schema(description = "User password", example = "password123")
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @Schema(description = "User role", example = "CUSTOMER")
        @NotNull(message = "Role is required")
        Role role
) {
}