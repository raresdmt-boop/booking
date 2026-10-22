package com.barbershop.booking.users.services;

import com.barbershop.booking.users.dtos.ChangePasswordRequest;
import com.barbershop.booking.users.dtos.UserCreateRequest;
import com.barbershop.booking.users.dtos.UserResponse;
import com.barbershop.booking.users.dtos.UserUpdateRequest;
import com.barbershop.booking.users.exceptions.UserEmailAlreadyExists;
import com.barbershop.booking.users.exceptions.UserNotFound;
import com.barbershop.booking.users.models.User;
import com.barbershop.booking.users.repository.UserRepository;
import com.barbershop.booking.users.services.interfaces.UserCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;

    public UserCommandServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserResponse addUser(UserCreateRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new UserEmailAlreadyExists();
        }

        User user = new User(
                request.email(),
                request.password(),
                request.role()
        );

        return UserResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(UserNotFound::new);

        if (request.email() != null && !request.email().isBlank()) {
            if (!request.email().equals(user.getEmail())
                    && userRepository.existsByEmail(request.email())) {
                throw new UserEmailAlreadyExists();
            }

            user.setEmail(request.email());
        }

        if (request.role() != null) {
            user.setRole(request.role());
        }

        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(UserNotFound::new);

        UserResponse deleted = UserResponse.from(user);

        userRepository.delete(user);

        return deleted;
    }

    @Override
    @Transactional
    public UserResponse changePassword(Long id, ChangePasswordRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(UserNotFound::new);

        user.setPasswordHash(request.newPassword());

        return UserResponse.from(user);
    }
}