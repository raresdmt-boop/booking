package com.barbershop.booking.appointments.exceptions;

public class AppointmentProcedureNotFound extends RuntimeException {

    public AppointmentProcedureNotFound() {
        super(AppointmentExceptionConstants.APPOINTMENT_PROCEDURE_NOT_FOUND);
    }
}