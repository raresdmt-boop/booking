package com.barbershop.booking.customers.exceptions;

public class CustomerPhoneAlreadyExists extends RuntimeException {

    public CustomerPhoneAlreadyExists() {
        super(CustomerExceptionConstants.CUSTOMER_PHONE_ALREADY_EXISTS);
    }
}