package com.barbershop.booking.users.exceptions;

public class UserEmailAlreadyExists extends RuntimeException {

    public UserEmailAlreadyExists() {
        super(UserExceptionConstants.USER_EMAIL_ALREADY_EXISTS);
    }
}