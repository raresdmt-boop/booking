package com.barbershop.booking.users.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("User - unit tests")
public class UserTest
{

    @Test
    @DisplayName("Constructor testing")
    void UserTest()
    {
        User user = new User(
                "test@gmail.com",
                "hashedPassword123",
                Role.CUSTOMER
        );

        assertThat(user.getId()).isNull();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getEmail().equals("test@gmail.com"));
    }

}
