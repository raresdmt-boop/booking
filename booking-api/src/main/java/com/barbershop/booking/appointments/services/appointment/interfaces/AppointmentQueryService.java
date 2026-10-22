
package com.barbershop.booking.appointments.services.appointment.interfaces;

import com.barbershop.booking.appointments.dtos.appointment.AppointmentResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentQueryService {

    AppointmentResponse getAppointmentById(Long id);

    List<AppointmentResponse> getAppointments();

    List<AppointmentResponse> getAppointmentsByCustomerId(Long customerId);

    List<AppointmentResponse> getAppointmentsByBarberId(Long barberId);

    List<AppointmentResponse> getAppointmentsByBarberIdAndPeriod(
            Long barberId,
            LocalDateTime start,
            LocalDateTime end
    );
}
