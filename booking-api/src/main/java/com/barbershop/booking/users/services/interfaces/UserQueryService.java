package com.barbershop.booking.users.services.interfaces;

import com.barbershop.booking.users.dtos.UserResponse;

import java.util.List;

public interface UserQueryService {

    UserResponse getUserById(Long id);

    List<UserResponse> getUsers();
}