
package com.barbershop.booking.appointments.exceptions;

public class AppointmentNotFound extends RuntimeException {

    public AppointmentNotFound() {
        super(AppointmentExceptionConstants.APPOINTMENT_ID_NOT_FOUND);
    }
}
