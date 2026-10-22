package com.barbershop.booking.users.exceptions;

public class UserNotFound extends RuntimeException {

    public UserNotFound() {
        super(UserExceptionConstants.USER_ID_NOT_FOUND);
    }
}