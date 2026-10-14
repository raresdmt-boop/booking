
package com.barbershop.booking.appointments.services;

import com.barbershop.booking.appointments.models.Appointment;
import java.util.List;

public interface AppointmentQueryService {

    Appointment getAppointmentById(Long id);

    List<Appointment> getAllAppointments();

}
