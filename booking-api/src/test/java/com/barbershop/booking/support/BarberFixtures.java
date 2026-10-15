package com.barbershop.booking.support;

import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.users.models.Role;
import com.barbershop.booking.users.models.User;
import org.springframework.test.util.ReflectionTestUtils;

public final class BarberFixtures {

    public static final Long KNOWN_ID = 1L;
    public static final Long OTHER_ID = 2L;

    private BarberFixtures() {
    }

    public static Barber barber() {
        User user = new User(
                "barber@gmail.com",
                "hashedPassword123",
                Role.BARBER
        );

        return new Barber(
                "Andrei",
                "Ionescu",
                "0723456789",
                user
        );
    }

    public static Barber withId(Long id) {
        Barber barber = barber();
        ReflectionTestUtils.setField(barber, "id", id);
        return barber;
    }

    public static Barber persisted() {
        return withId(KNOWN_ID);
    }
}