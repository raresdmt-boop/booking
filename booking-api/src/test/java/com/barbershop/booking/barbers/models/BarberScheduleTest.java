package com.barbershop.booking.barbers.models;

import com.barbershop.booking.support.BarberFixtures;
import com.barbershop.booking.support.BarberScheduleFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class BarberScheduleTest {

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        void constructor_initializesFields() {
            Barber barber = BarberFixtures.persisted();

            BarberSchedule schedule = new BarberSchedule(
                    barber,
                    1,
                    LocalTime.of(9, 0),
                    LocalTime.of(17, 0)
            );

            assertThat(schedule.getId()).isNull();
            assertThat(schedule.getBarber()).isSameAs(barber);
            assertThat(schedule.getDayOfWeek()).isEqualTo(1);
            assertThat(schedule.getStartTime()).isEqualTo(LocalTime.of(9, 0));
            assertThat(schedule.getEndTime()).isEqualTo(LocalTime.of(17, 0));
        }
    }

    @Nested
    @DisplayName("Equals")
    class EqualsTests {

        @Test
        void equals_sameInstance_returnsTrue() {
            BarberSchedule schedule = BarberScheduleFixtures.persisted();

            assertThat(schedule).isEqualTo(schedule);
        }

        @Test
        void equals_sameId_returnsTrue() {
            BarberSchedule first = BarberScheduleFixtures.withId(1L);
            BarberSchedule second = BarberScheduleFixtures.withId(1L);

            assertThat(first).isEqualTo(second);
        }

        @Test
        void equals_differentIds_returnsFalse() {
            BarberSchedule first = BarberScheduleFixtures.withId(1L);
            BarberSchedule second = BarberScheduleFixtures.withId(2L);

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_bothIdsNull_returnsFalse() {
            BarberSchedule first = BarberScheduleFixtures.barberSchedule();
            BarberSchedule second = BarberScheduleFixtures.barberSchedule();

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_oneIdNull_returnsFalse() {
            BarberSchedule first = BarberScheduleFixtures.persisted();
            BarberSchedule second = BarberScheduleFixtures.barberSchedule();

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_null_returnsFalse() {
            BarberSchedule schedule = BarberScheduleFixtures.persisted();

            assertThat(schedule).isNotEqualTo(null);
        }

        @Test
        void equals_differentClass_returnsFalse() {
            BarberSchedule schedule = BarberScheduleFixtures.persisted();

            assertThat(schedule).isNotEqualTo("text");
        }
    }

    @Nested
    @DisplayName("HashCode")
    class HashCodeTests {

        @Test
        void hashCode_sameId_returnsSameHashCode() {
            BarberSchedule first = BarberScheduleFixtures.withId(1L);
            BarberSchedule second = BarberScheduleFixtures.withId(1L);

            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }

        @Test
        void hashCode_differentIds_returnsSameHashCode() {
            BarberSchedule first = BarberScheduleFixtures.withId(1L);
            BarberSchedule second = BarberScheduleFixtures.withId(2L);

            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToStringTests {

        @Test
        void toString_containsRelevantFields() {
            BarberSchedule schedule = BarberScheduleFixtures.persisted();

            String result = schedule.toString();

            assertThat(result)
                    .contains("id=1")
                    .contains("dayOfWeek=1")
                    .contains("startTime=09:00")
                    .contains("endTime=17:00");
        }
    }

    @Nested
    @DisplayName("Setters")
    class SetterTests {

        @Test
        void setDayOfWeek_changesDay() {
            BarberSchedule schedule = BarberScheduleFixtures.persisted();

            schedule.setDayOfWeek(5);

            assertThat(schedule.getDayOfWeek()).isEqualTo(5);
        }

        @Test
        void updateWorkingHours_changesStartAndEndTime() {
            BarberSchedule schedule = BarberScheduleFixtures.persisted();

            schedule.updateWorkingHours(
                    LocalTime.of(10, 0),
                    LocalTime.of(18, 0)
            );

            assertThat(schedule.getStartTime())
                    .isEqualTo(LocalTime.of(10, 0));

            assertThat(schedule.getEndTime())
                    .isEqualTo(LocalTime.of(18, 0));
        }
    }
}