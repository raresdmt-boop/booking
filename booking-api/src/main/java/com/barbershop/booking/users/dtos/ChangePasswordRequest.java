package com.barbershop.booking.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "ChangePasswordRequest", description = "Data required to change a user's password")
public record ChangePasswordRequest(

        @Schema(description = "New password", example = "newPassword123", minLength = 8)
        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String newPassword

) {
}