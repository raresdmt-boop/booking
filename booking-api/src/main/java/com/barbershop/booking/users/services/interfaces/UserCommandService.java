package com.barbershop.booking.users.services.interfaces;

import com.barbershop.booking.users.dtos.ChangePasswordRequest;
import com.barbershop.booking.users.dtos.UserCreateRequest;
import com.barbershop.booking.users.dtos.UserResponse;
import com.barbershop.booking.users.dtos.UserUpdateRequest;

public interface UserCommandService {

    UserResponse addUser(UserCreateRequest request);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    UserResponse deleteUser(Long id);

    UserResponse changePassword(Long id, ChangePasswordRequest request);
}