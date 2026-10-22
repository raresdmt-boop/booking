package com.barbershop.booking.customers.exceptions;

public class CustomerNotFound extends RuntimeException {

    public CustomerNotFound() {
        super(CustomerExceptionConstants.CUSTOMER_ID_NOT_FOUND);
    }
}