package com.barbershop.booking.appointments.dtos.appointmentprocedure;

import com.barbershop.booking.appointments.models.AppointmentProcedure;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Appointment procedure details")
public record AppointmentProcedureResponse(

        Long id,
        Long appointmentId,
        Long procedureId,
        BigDecimal price,
        Integer durationMinutes

) {

    public static AppointmentProcedureResponse from(
            AppointmentProcedure appointmentProcedure) {

        return new AppointmentProcedureResponse(
                appointmentProcedure.getId(),
                appointmentProcedure.getAppointment().getId(),
                appointmentProcedure.getProcedure().getId(),
                appointmentProcedure.getPrice(),
                appointmentProcedure.getDurationMinutes()
        );
    }
}