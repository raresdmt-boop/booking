package com.barbershop.booking.barbers.exceptions;

public class BarberNotActive extends RuntimeException {
    public BarberNotActive() {
        super(BarberExceptionConstants.BARBER_NOT_ACTIVE);
    }
}
