package com.barbershop.booking.appointments.exceptions;

public class AppointmentInvalidStatus extends RuntimeException {

    public AppointmentInvalidStatus() {
        super(AppointmentExceptionConstants.APPOINTMENT_INVALID_STATUS);
    }
}