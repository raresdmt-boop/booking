package com.barbershop.booking.support;

import com.barbershop.booking.appointments.models.Appointment;
import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.customers.models.Customer;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class AppointmentFixtures {

    public static final Long KNOWN_ID = 1L;
    public static final Long OTHER_ID = 2L;



    private AppointmentFixtures() {
    }

    public static Appointment appointment(Customer customer, Barber barber) {

            LocalDateTime startTime = LocalDateTime.now()
                    .plusDays(7)
                    .withHour(10)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            return new Appointment(
                    customer,
                    barber,
                    startTime,
                    startTime.plusMinutes(30),
                    "Test appointment"
            );
        }

    public static Appointment withId(
            Long id, Customer customer, Barber barber) {

        Appointment appointment = appointment(customer, barber);

        ReflectionTestUtils.setField(appointment, "id", id);

        return appointment;
    }

    public static Appointment persisted(Customer customer, Barber barber) {
        return withId(KNOWN_ID, customer, barber);
    }
}