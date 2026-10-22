
package com.barbershop.booking.appointments.services.appointment.interfaces;

import com.barbershop.booking.appointments.dtos.appointment.AppointmentCreateRequest;
import com.barbershop.booking.appointments.dtos.appointment.AppointmentResponse;
import com.barbershop.booking.appointments.dtos.appointment.AppointmentUpdateRequest;

public interface AppointmentCommandService {

    AppointmentResponse addAppointment(AppointmentCreateRequest request);

    AppointmentResponse updateAppointment(Long id, AppointmentUpdateRequest request);

    AppointmentResponse confirmAppointment(Long id);

    AppointmentResponse cancelAppointment(Long id);

    AppointmentResponse completeAppointment(Long id);
}
