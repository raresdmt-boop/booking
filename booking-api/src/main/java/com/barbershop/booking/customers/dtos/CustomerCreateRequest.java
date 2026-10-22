package com.barbershop.booking.customers.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CustomerCreateRequest",
        description = "Data required to create a customer profile")
public record CustomerCreateRequest(

        @Schema(description = "Associated user ID", example = "1")
        @NotNull(message = "User ID is required")
        Long userId,

        @Schema(description = "Customer first name", example = "Ion")
        @NotBlank(message = "First name is required")
        String firstName,

        @Schema(description = "Customer last name", example = "Popescu")
        @NotBlank(message = "Last name is required")
        String lastName,

        @Schema(description = "Customer phone number", example = "0712345678")
        @NotBlank(message = "Phone is required")
        String phone

) {
}