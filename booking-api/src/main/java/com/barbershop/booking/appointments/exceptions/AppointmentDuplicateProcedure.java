package com.barbershop.booking.appointments.exceptions;

public class AppointmentDuplicateProcedure extends RuntimeException {

    public AppointmentDuplicateProcedure() {
        super(AppointmentExceptionConstants.APPOINTMENT_DUPLICATE_PROCEDURE);
    }
}