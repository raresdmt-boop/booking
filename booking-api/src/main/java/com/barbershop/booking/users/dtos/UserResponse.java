package com.barbershop.booking.users.dtos;

import com.barbershop.booking.users.models.Role;
import com.barbershop.booking.users.models.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "UserResponse", description = "User information returned by the API")
public record UserResponse(

        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "User email", example = "test@gmail.com")
        String email,

        @Schema(description = "User role", example = "CUSTOMER")
        Role role,

        @Schema(description = "User creation date")
        LocalDateTime createdAt

) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}