
package com.barbershop.booking.procedures.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(name = "ProcedureCreateRequest",
        description = "Data required to create a procedure")
public record ProcedureCreateRequest(

        @Schema(description = "Associated barber ID", example = "1")
        @NotNull(message = "Barber ID is required")
        Long barberId,

        @Schema(description = "Procedure name", example = "Haircut")
        @NotBlank(message = "Procedure name is required")
        String name,

        @Schema(description = "Procedure description", example = "Classic men's haircut")
        String description,

        @Schema(description = "Duration in minutes", example = "30")
        @NotNull(message = "Duration is required")
        @Positive(message = "Duration must be greater than zero")
        Integer durationMinutes,

        @Schema(description = "Procedure price", example = "60.00")
        @NotNull(message = "Price is required")
        @PositiveOrZero(message = "Price cannot be negative")
        BigDecimal price

) {
}
