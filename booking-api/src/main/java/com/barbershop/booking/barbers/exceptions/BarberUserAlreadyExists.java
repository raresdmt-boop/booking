package com.barbershop.booking.barbers.exceptions;

public class BarberUserAlreadyExists extends RuntimeException {

    public BarberUserAlreadyExists() {
        super(BarberExceptionConstants.BARBER_USER_ALREADY_EXISTS);
    }
}