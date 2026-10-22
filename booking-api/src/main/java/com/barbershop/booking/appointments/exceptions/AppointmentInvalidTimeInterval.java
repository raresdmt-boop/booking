
package com.barbershop.booking.appointments.exceptions;

public class AppointmentInvalidTimeInterval extends RuntimeException {

    public AppointmentInvalidTimeInterval() {
        super(AppointmentExceptionConstants.APPOINTMENT_INVALID_TIME_INTERVAL);
    }
}
