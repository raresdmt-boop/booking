package com.barbershop.booking.support;

import com.barbershop.booking.users.models.Role;
import com.barbershop.booking.users.models.User;
import org.springframework.test.util.ReflectionTestUtils;

public final class UserFixtures {

    public static final Long KNOWN_ID = 1L;
    public static final Long OTHER_ID = 2L;

    private UserFixtures() {
    }

    public static User user() {
        return new User(
                "test@gmail.com",
                "hashedPassword123",
                Role.CUSTOMER
        );
    }

    public static User withId(Long id) {
        User user = user();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public static User persisted() {
        return withId(KNOWN_ID);
    }
}