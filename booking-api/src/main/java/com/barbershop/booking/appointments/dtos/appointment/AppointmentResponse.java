
package com.barbershop.booking.appointments.dtos.appointment;

import com.barbershop.booking.appointments.dtos.appointmentprocedure.AppointmentProcedureResponse;
import com.barbershop.booking.appointments.models.Appointment;
import com.barbershop.booking.appointments.models.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(
        name = "AppointmentResponse",
        description = "Appointment information"
)
public record AppointmentResponse(

        Long id,
        Long customerId,
        Long barberId,
        Long recurringAppointmentId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        String notes,
        LocalDateTime createdAt,
        List<AppointmentProcedureResponse> procedures

) {

    public static AppointmentResponse from(
            Appointment appointment,
            List<AppointmentProcedureResponse> procedures) {

        Long recurringAppointmentId = null;

        if (appointment.getRecurringAppointment() != null) {
            recurringAppointmentId = appointment.getRecurringAppointment().getId();
        }

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getCustomer().getId(),
                appointment.getBarber().getId(),
                recurringAppointmentId,
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCreatedAt(),
                procedures
        );
    }

    public static AppointmentResponse from(Appointment appointment) {
        return from(appointment, List.of());
    }
}
