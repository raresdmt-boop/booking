package com.barbershop.booking.barbers.models;

import com.barbershop.booking.support.BarberFixtures;
import com.barbershop.booking.support.BarberTimeOffFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BarberTimeOffTest {

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        void constructor_initializesFields() {
            Barber barber = BarberFixtures.persisted();

            LocalDateTime start = LocalDateTime.of(2026, 10, 20, 9, 0);
            LocalDateTime end = LocalDateTime.of(2026, 10, 22, 17, 0);

            BarberTimeOff timeOff = new BarberTimeOff(
                    barber,
                    start,
                    end,
                    "Vacation"
            );

            assertThat(timeOff.getId()).isNull();
            assertThat(timeOff.getBarber()).isSameAs(barber);
            assertThat(timeOff.getStartDatetime()).isEqualTo(start);
            assertThat(timeOff.getEndDatetime()).isEqualTo(end);
            assertThat(timeOff.getReason()).isEqualTo("Vacation");
        }
    }

    @Nested
    @DisplayName("Equals")
    class EqualsTests {

        @Test
        void equals_sameInstance_returnsTrue() {
            BarberTimeOff timeOff = BarberTimeOffFixtures.persisted();

            assertThat(timeOff).isEqualTo(timeOff);
        }

        @Test
        void equals_sameId_returnsTrue() {
            BarberTimeOff first = BarberTimeOffFixtures.withId(1L);
            BarberTimeOff second = BarberTimeOffFixtures.withId(1L);

            assertThat(first).isEqualTo(second);
        }

        @Test
        void equals_differentIds_returnsFalse() {
            BarberTimeOff first = BarberTimeOffFixtures.withId(1L);
            BarberTimeOff second = BarberTimeOffFixtures.withId(2L);

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_bothIdsNull_returnsFalse() {
            BarberTimeOff first = BarberTimeOffFixtures.barberTimeOff();
            BarberTimeOff second = BarberTimeOffFixtures.barberTimeOff();

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_oneIdNull_returnsFalse() {
            BarberTimeOff first = BarberTimeOffFixtures.persisted();
            BarberTimeOff second = BarberTimeOffFixtures.barberTimeOff();

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_null_returnsFalse() {
            BarberTimeOff timeOff = BarberTimeOffFixtures.persisted();

            assertThat(timeOff).isNotEqualTo(null);
        }

        @Test
        void equals_differentClass_returnsFalse() {
            BarberTimeOff timeOff = BarberTimeOffFixtures.persisted();

            assertThat(timeOff).isNotEqualTo("text");
        }
    }

    @Nested
    @DisplayName("HashCode")
    class HashCodeTests {

        @Test
        void hashCode_sameId_returnsSameHashCode() {
            BarberTimeOff first = BarberTimeOffFixtures.withId(1L);
            BarberTimeOff second = BarberTimeOffFixtures.withId(1L);

            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }

        @Test
        void hashCode_differentIds_returnsSameHashCode() {
            BarberTimeOff first = BarberTimeOffFixtures.withId(1L);
            BarberTimeOff second = BarberTimeOffFixtures.withId(2L);

            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToStringTests {

        @Test
        void toString_containsRelevantFields() {
            BarberTimeOff timeOff = BarberTimeOffFixtures.persisted();

            String result = timeOff.toString();

            assertThat(result)
                    .contains("id=1")
                    .contains("startDatetime=2026-10-20T09:00")
                    .contains("endDatetime=2026-10-22T17:00")
                    .contains("reason='Vacation'");
        }
    }

    @Nested
    @DisplayName("Setters")
    class SetterTests {

        @Test
        void setStartDatetime_changesStartDatetime() {
            BarberTimeOff timeOff = BarberTimeOffFixtures.persisted();

            LocalDateTime newStart = LocalDateTime.of(2026, 11, 1, 9, 0);

            timeOff.setStartDatetime(newStart);

            assertThat(timeOff.getStartDatetime()).isEqualTo(newStart);
        }

        @Test
        void setEndDatetime_changesEndDatetime() {
            BarberTimeOff timeOff = BarberTimeOffFixtures.persisted();

            LocalDateTime newEnd = LocalDateTime.of(2026, 11, 5, 17, 0);

            timeOff.setEndDatetime(newEnd);

            assertThat(timeOff.getEndDatetime()).isEqualTo(newEnd);
        }

        @Test
        void setReason_changesReason() {
            BarberTimeOff timeOff = BarberTimeOffFixtures.persisted();

            timeOff.setReason("Medical leave");

            assertThat(timeOff.getReason()).isEqualTo("Medical leave");
        }
    }
}