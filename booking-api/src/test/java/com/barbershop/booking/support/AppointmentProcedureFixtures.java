package com.barbershop.booking.support;

import com.barbershop.booking.appointments.models.Appointment;
import com.barbershop.booking.appointments.models.AppointmentProcedure;
import com.barbershop.booking.procedures.models.Procedure;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

public final class AppointmentProcedureFixtures {

    public static final Long KNOWN_ID = 1L;
    public static final Long OTHER_ID = 2L;

    private AppointmentProcedureFixtures() {
    }

    public static AppointmentProcedure appointmentProcedure(
            Appointment appointment, Procedure procedure) {

        return new AppointmentProcedure(
                appointment,
                procedure,
                new BigDecimal("50.00"),
                30
        );
    }

    public static AppointmentProcedure withId(
            Long id, Appointment appointment, Procedure procedure) {

        AppointmentProcedure appointmentProcedure =
                appointmentProcedure(appointment, procedure);

        ReflectionTestUtils.setField(appointmentProcedure, "id", id);

        return appointmentProcedure;
    }

    public static AppointmentProcedure persisted(
            Appointment appointment, Procedure procedure) {

        return withId(KNOWN_ID, appointment, procedure);
    }
}