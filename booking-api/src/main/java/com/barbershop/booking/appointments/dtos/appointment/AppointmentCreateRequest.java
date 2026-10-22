
package com.barbershop.booking.appointments.dtos.appointment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "AppointmentCreateRequest",
        description = "Data required to create an appointment")
public record AppointmentCreateRequest(

        @Schema(description = "Customer ID", example = "1")
        @NotNull(message = "Customer ID is required")
        Long customerId,

        @Schema(description = "Barber ID", example = "1")
        @NotNull(message = "Barber ID is required")
        Long barberId,

        @Schema(description = "Appointment start time",
                example = "2026-11-10T10:00:00")
        @NotNull(message = "Start time is required")
        LocalDateTime startTime,


        @Schema(description = "Optional appointment notes",
                example = "Short haircut")
        String notes,

        @NotEmpty
        @Schema(
                description = "IDs of the procedures selected for the appointment",
                example = "[1, 2, 3]"
        )
        List<@NotNull Long> procedureIds

) {
}
