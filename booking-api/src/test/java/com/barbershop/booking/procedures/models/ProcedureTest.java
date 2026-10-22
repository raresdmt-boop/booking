package com.barbershop.booking.procedures.models;

import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.support.BarberFixtures;
import com.barbershop.booking.support.ProcedureFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProcedureTest {

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        void constructor_initializesFields() {
            Barber barber = BarberFixtures.persisted();

            Procedure procedure = new Procedure(
                    "Haircut",
                    "Men's haircut",
                    30,
                    new BigDecimal("50.00"),
                    barber
            );

            assertThat(procedure.getId()).isNull();
            assertThat(procedure.getName()).isEqualTo("Haircut");
            assertThat(procedure.getDescription()).isEqualTo("Men's haircut");
            assertThat(procedure.getDurationMinutes()).isEqualTo(30);
            assertThat(procedure.getPrice()).isEqualByComparingTo("50.00");
            assertThat(procedure.getBarber()).isSameAs(barber);
            assertThat(procedure.isActive()).isTrue();
            assertThat(procedure.getAppointmentProcedures()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Equals")
    class EqualsTests {

        @Test
        void equals_sameInstance_returnsTrue() {
            Procedure procedure = ProcedureFixtures.persisted();

            assertThat(procedure).isEqualTo(procedure);
        }

        @Test
        void equals_sameId_returnsTrue() {
            Procedure first = ProcedureFixtures.withId(1L);
            Procedure second = ProcedureFixtures.withId(1L);

            assertThat(first).isEqualTo(second);
        }

        @Test
        void equals_differentIds_returnsFalse() {
            Procedure first = ProcedureFixtures.withId(1L);
            Procedure second = ProcedureFixtures.withId(2L);

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_bothIdsNull_returnsFalse() {
            Procedure first = ProcedureFixtures.procedure();
            Procedure second = ProcedureFixtures.procedure();

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_oneIdNull_returnsFalse() {
            Procedure first = ProcedureFixtures.persisted();
            Procedure second = ProcedureFixtures.procedure();

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_null_returnsFalse() {
            Procedure procedure = ProcedureFixtures.persisted();

            assertThat(procedure).isNotEqualTo(null);
        }

        @Test
        void equals_differentClass_returnsFalse() {
            Procedure procedure = ProcedureFixtures.persisted();

            assertThat(procedure).isNotEqualTo("text");
        }
    }

    @Nested
    @DisplayName("HashCode")
    class HashCodeTests {

        @Test
        void hashCode_sameId_returnsSameHashCode() {
            Procedure first = ProcedureFixtures.withId(1L);
            Procedure second = ProcedureFixtures.withId(1L);

            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }

        @Test
        void hashCode_differentIds_returnsSameHashCode() {
            Procedure first = ProcedureFixtures.withId(1L);
            Procedure second = ProcedureFixtures.withId(2L);

            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToStringTests {

        @Test
        void toString_containsRelevantFields() {
            Procedure procedure = ProcedureFixtures.persisted();

            String result = procedure.toString();

            assertThat(result)
                    .contains("id=1")
                    .contains("name='Haircut'")
                    .contains("description='Men's haircut'")
                    .contains("durationMinutes=30")
                    .contains("price=50.00")
                    .contains("active=true")
                    .contains("barberId=1");
        }
    }

    @Nested
    @DisplayName("Setters")
    class SetterTests {

        @Test
        void setName_changesName() {
            Procedure procedure = ProcedureFixtures.persisted();

            procedure.setName("Beard Trim");

            assertThat(procedure.getName()).isEqualTo("Beard Trim");
        }

        @Test
        void setDescription_changesDescription() {
            Procedure procedure = ProcedureFixtures.persisted();

            procedure.setDescription("Beard trimming service");

            assertThat(procedure.getDescription())
                    .isEqualTo("Beard trimming service");
        }

        @Test
        void setDurationMinutes_changesDuration() {
            Procedure procedure = ProcedureFixtures.persisted();

            procedure.setDurationMinutes(45);

            assertThat(procedure.getDurationMinutes()).isEqualTo(45);
        }

        @Test
        void setPrice_changesPrice() {
            Procedure procedure = ProcedureFixtures.persisted();

            procedure.setPrice(new BigDecimal("75.00"));

            assertThat(procedure.getPrice())
                    .isEqualByComparingTo("75.00");
        }

        @Test
        void setActive_changesActiveStatus() {
            Procedure procedure = ProcedureFixtures.persisted();

            procedure.setActive(false);

            assertThat(procedure.isActive()).isFalse();
        }
    }
}