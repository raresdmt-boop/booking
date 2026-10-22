package com.barbershop.booking.appointments.services.appointment.interfaces;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentAvailabilityService {

    List<LocalTime> getAvailableSlots(
            Long barberId,
            LocalDate date,
            List<Long> procedureIds
    );
}