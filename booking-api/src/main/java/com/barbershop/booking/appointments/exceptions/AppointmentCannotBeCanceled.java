
package com.barbershop.booking.appointments.exceptions;

public class AppointmentCannotBeCanceled extends RuntimeException {

    public AppointmentCannotBeCanceled() {
        super(AppointmentExceptionConstants.APPOINTMENT_CANNOT_BE_CANCELED);
    }
}
