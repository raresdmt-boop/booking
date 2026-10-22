package com.barbershop.booking.appointments.dtos.appointmentprocedure;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(description = "Request for updating an appointment procedure")
public record AppointmentProcedureUpdateRequest(

        @PositiveOrZero
        @Schema(description = "Updated procedure price", example = "60.00")
        BigDecimal price,

        @Positive
        @Schema(description = "Updated procedure duration in minutes", example = "45")
        Integer durationMinutes

) {
}