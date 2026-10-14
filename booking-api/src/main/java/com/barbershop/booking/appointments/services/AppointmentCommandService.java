
package com.barbershop.booking.appointments.services;

import com.barbershop.booking.appointments.models.Appointment;

public interface AppointmentCommandService {

    Appointment createAppointment(Appointment appointment);

    Appointment updateAppointment(Long id, Appointment appointment);

    void cancelAppointment(Long id);

}
