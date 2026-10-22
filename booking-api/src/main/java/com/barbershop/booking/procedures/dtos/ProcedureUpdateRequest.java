
package com.barbershop.booking.procedures.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(name = "ProcedureUpdateRequest",
        description = "Data used to update a procedure")
public record ProcedureUpdateRequest(

        @Schema(description = "Procedure name", example = "Premium Haircut")
        String name,

        @Schema(description = "Procedure description", example = "Haircut and styling")
        String description,

        @Schema(description = "Duration in minutes", example = "45")
        @Positive(message = "Duration must be greater than zero")
        Integer durationMinutes,

        @Schema(description = "Procedure price", example = "80.00")
        @PositiveOrZero(message = "Price cannot be negative")
        BigDecimal price,

        @Schema(description = "Whether the procedure is active", example = "true")
        Boolean active

) {
}
