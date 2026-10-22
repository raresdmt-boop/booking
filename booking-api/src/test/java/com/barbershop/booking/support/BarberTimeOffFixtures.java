package com.barbershop.booking.support;

import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.barbers.models.BarberTimeOff;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

public final class BarberTimeOffFixtures {

    public static final Long KNOWN_ID = 1L;
    public static final Long OTHER_ID = 2L;

    private BarberTimeOffFixtures() {
    }

    public static BarberTimeOff barberTimeOff() {
        Barber barber = BarberFixtures.persisted();

        return new BarberTimeOff(
                barber,
                LocalDateTime.of(2026, 10, 20, 9, 0),
                LocalDateTime.of(2026, 10, 22, 17, 0),
                "Vacation"
        );
    }

    public static BarberTimeOff withId(Long id) {
        BarberTimeOff barberTimeOff = barberTimeOff();

        ReflectionTestUtils.setField(barberTimeOff, "id", id);

        return barberTimeOff;
    }

    public static BarberTimeOff persisted() {
        return withId(KNOWN_ID);
    }
}