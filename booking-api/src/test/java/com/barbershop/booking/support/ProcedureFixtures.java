package com.barbershop.booking.support;

import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.procedures.models.Procedure;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

public final class ProcedureFixtures {

    public static final Long KNOWN_ID = 1L;
    public static final Long OTHER_ID = 2L;

    private ProcedureFixtures() {
    }

    public static Procedure procedure() {
        return new Procedure(
                "Haircut",
                "Men's haircut",
                30,
                new BigDecimal("50.00"),
                BarberFixtures.persisted()
        );
    }

    public static Procedure withId(Long id) {
        Procedure procedure = procedure();

        ReflectionTestUtils.setField(procedure, "id", id);

        return procedure;
    }

    public static Procedure persisted() {
        return withId(KNOWN_ID);
    }
}