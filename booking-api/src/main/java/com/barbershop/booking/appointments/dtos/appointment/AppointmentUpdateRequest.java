
package com.barbershop.booking.appointments.dtos.appointment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "AppointmentUpdateRequest",
        description = "Data used to update an appointment")
public record AppointmentUpdateRequest(

        @Schema(description = "Appointment start time",
                example = "2026-11-10T11:00:00")
        LocalDateTime startTime,

        @Schema(description = "Appointment notes",
                example = "Haircut and beard trim")
        String notes

) {
}
