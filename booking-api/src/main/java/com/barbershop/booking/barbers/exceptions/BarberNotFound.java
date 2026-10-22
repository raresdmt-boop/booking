package com.barbershop.booking.barbers.exceptions;

public class BarberNotFound extends RuntimeException {

    public BarberNotFound() {
        super(BarberExceptionConstants.BARBER_ID_NOT_FOUND);
    }
}