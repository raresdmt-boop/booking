package com.barbershop.booking.appointments.exceptions;

public class AppointmentProceduresRequired extends RuntimeException {

    public AppointmentProceduresRequired() {
        super(AppointmentExceptionConstants.APPOINTMENT_PROCEDURES_REQUIRED);
    }
}