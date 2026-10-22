package com.barbershop.booking.support;

import com.barbershop.booking.appointments.models.RecurringAppointment;
import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.customers.models.Customer;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;

public final class RecurringAppointmentFixtures {

    public static final Long KNOWN_ID = 1L;
    public static final Long OTHER_ID = 2L;

    private RecurringAppointmentFixtures() {
    }

    public static RecurringAppointment recurringAppointment() {
        Customer customer = CustomerFixtures.persisted();
        Barber barber = BarberFixtures.persisted();

        return new RecurringAppointment(
                customer,
                2,
                LocalDate.of(2026, 10, 20),
                LocalDate.of(2026, 12, 20),
                LocalTime.of(10, 0),
                barber
        );
    }

    public static RecurringAppointment withId(Long id) {
        RecurringAppointment recurringAppointment = recurringAppointment();

        ReflectionTestUtils.setField(recurringAppointment, "id", id);

        return recurringAppointment;
    }

    public static RecurringAppointment persisted() {
        return withId(KNOWN_ID);
    }
}