
package com.barbershop.booking.barbers.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "BarberCreateRequest",
        description = "Data required to create a barber profile")
public record BarberCreateRequest(

        @Schema(description = "Associated user ID", example = "1")
        @NotNull(message = "User ID is required")
        Long userId,

        @Schema(description = "Barber first name", example = "Andrei")
        @NotBlank(message = "First name is required")
        String firstName,

        @Schema(description = "Barber last name", example = "Popescu")
        @NotBlank(message = "Last name is required")
        String lastName,

        @Schema(description = "Barber phone number", example = "0712345678")
        @NotBlank(message = "Phone is required")
        String phone

) {
}
