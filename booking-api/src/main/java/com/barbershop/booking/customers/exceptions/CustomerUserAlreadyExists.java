package com.barbershop.booking.customers.exceptions;

public class CustomerUserAlreadyExists extends RuntimeException {

    public CustomerUserAlreadyExists() {
        super(CustomerExceptionConstants.CUSTOMER_USER_ALREADY_EXISTS);
    }
}