
package com.barbershop.booking.appointments.dtos.appointmentprocedure;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request for adding a procedure to an appointment")
public record AppointmentProcedureCreateRequest(

        @NotNull
        @Schema(description = "Appointment ID", example = "1")
        Long appointmentId,

        @NotNull
        @Schema(description = "Procedure ID", example = "2")
        Long procedureId

) {
}