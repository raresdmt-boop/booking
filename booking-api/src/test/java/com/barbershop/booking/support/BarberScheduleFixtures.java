package com.barbershop.booking.support;

import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.barbers.models.BarberSchedule;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;

public final class BarberScheduleFixtures {

    public static final Long KNOWN_ID = 1L;
    public static final Long OTHER_ID = 2L;

    private BarberScheduleFixtures() {
    }

    public static BarberSchedule barberSchedule() {
        Barber barber = BarberFixtures.persisted();

        return new BarberSchedule(
                barber,
                1,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
    }

    public static BarberSchedule withId(Long id) {
        BarberSchedule barberSchedule = barberSchedule();

        ReflectionTestUtils.setField(barberSchedule, "id", id);

        return barberSchedule;
    }

    public static BarberSchedule persisted() {
        return withId(KNOWN_ID);
    }
}