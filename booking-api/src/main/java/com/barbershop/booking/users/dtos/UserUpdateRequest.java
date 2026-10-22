package com.barbershop.booking.users.dtos;

import com.barbershop.booking.users.models.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(name = "UserUpdateRequest", description = "Data used to update an existing user")
public record UserUpdateRequest(

        @Schema(description = "New email", example = "newemail@gmail.com")
        @Email(message = "Email must be valid")
        String email,

        @Schema(description = "New user role", example = "CUSTOMER")
        Role role
) {
}