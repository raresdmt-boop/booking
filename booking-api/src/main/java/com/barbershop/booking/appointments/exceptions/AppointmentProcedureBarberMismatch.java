package com.barbershop.booking.appointments.exceptions;

public class AppointmentProcedureBarberMismatch extends RuntimeException {
    public AppointmentProcedureBarberMismatch() {
        super(AppointmentExceptionConstants.APPOINTMENT_PROCEDURE_BARBER_MISMATCH);
    }
}
