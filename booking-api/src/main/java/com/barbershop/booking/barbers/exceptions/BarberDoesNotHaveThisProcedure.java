package com.barbershop.booking.barbers.exceptions;

public class BarberDoesNotHaveThisProcedure extends RuntimeException {
    public BarberDoesNotHaveThisProcedure() {
        super(BarberExceptionConstants.BARBER_DOES_NOT_HAVE_THIS_PROCEDURE);
    }
}
