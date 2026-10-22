package com.barbershop.booking.barbers.models;

import com.barbershop.booking.support.BarberFixtures;
import com.barbershop.booking.users.models.Role;
import com.barbershop.booking.users.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BarberTest {

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        void constructor_initializesFields() {
            User user = new User(
                    "barber@gmail.com",
                    "hashedPassword123",
                    Role.BARBER
            );

            LocalDateTime before = LocalDateTime.now();

            Barber barber = new Barber(
                    "Andrei",
                    "Ionescu",
                    "0723456789",
                    user
            );

            LocalDateTime after = LocalDateTime.now();

            assertThat(barber.getId()).isNull();
            assertThat(barber.getUser()).isSameAs(user);
            assertThat(barber.getFirstName()).isEqualTo("Andrei");
            assertThat(barber.getLastName()).isEqualTo("Ionescu");
            assertThat(barber.getPhone()).isEqualTo("0723456789");
            assertThat(barber.isActive()).isTrue();
            assertThat(barber.getCreatedAt()).isBetween(before, after);
            assertThat(barber.getAppointments()).isEmpty();
            assertThat(barber.getSchedules()).isEmpty();
            assertThat(barber.getTimeOffs()).isEmpty();
            assertThat(barber.getProcedures()).isEmpty();
            assertThat(barber.getRecurringAppointments()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Equals")
    class EqualsTests {

        @Test
        void equals_sameInstance_returnsTrue() {
            Barber barber = BarberFixtures.persisted();

            assertThat(barber).isEqualTo(barber);
        }

        @Test
        void equals_sameId_returnsTrue() {
            Barber first = BarberFixtures.withId(1L);
            Barber second = BarberFixtures.withId(1L);

            assertThat(first).isEqualTo(second);
        }

        @Test
        void equals_differentIds_returnsFalse() {
            Barber first = BarberFixtures.withId(1L);
            Barber second = BarberFixtures.withId(2L);

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_bothIdsNull_returnsFalse() {
            Barber first = BarberFixtures.barber();
            Barber second = BarberFixtures.barber();

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_oneIdNull_returnsFalse() {
            Barber first = BarberFixtures.persisted();
            Barber second = BarberFixtures.barber();

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_null_returnsFalse() {
            Barber barber = BarberFixtures.persisted();

            assertThat(barber).isNotEqualTo(null);
        }

        @Test
        void equals_differentClass_returnsFalse() {
            Barber barber = BarberFixtures.persisted();

            assertThat(barber).isNotEqualTo("text");
        }
    }

    @Nested
    @DisplayName("HashCode")
    class HashCodeTests {

        @Test
        void hashCode_sameId_returnsSameHashCode() {
            Barber first = BarberFixtures.withId(1L);
            Barber second = BarberFixtures.withId(1L);

            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }

        @Test
        void hashCode_differentIds_returnsSameHashCode() {
            Barber first = BarberFixtures.withId(1L);
            Barber second = BarberFixtures.withId(2L);

            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToStringTests {

        @Test
        void toString_containsRelevantFields() {
            Barber barber = BarberFixtures.persisted();

            String result = barber.toString();

            assertThat(result)
                    .contains("id=1")
                    .contains("firstName='Andrei'")
                    .contains("lastName='Ionescu'")
                    .contains("phone='0723456789'")
                    .contains("active=true")
                    .contains("userId=");
        }
    }

    @Nested
    @DisplayName("Setters")
    class SetterTests {

        @Test
        void setFirstName_changesFirstName() {
            Barber barber = BarberFixtures.persisted();

            barber.setFirstName("Mihai");

            assertThat(barber.getFirstName()).isEqualTo("Mihai");
        }

        @Test
        void setLastName_changesLastName() {
            Barber barber = BarberFixtures.persisted();

            barber.setLastName("Popescu");

            assertThat(barber.getLastName()).isEqualTo("Popescu");
        }

        @Test
        void setPhone_changesPhone() {
            Barber barber = BarberFixtures.persisted();

            barber.setPhone("0799999999");

            assertThat(barber.getPhone()).isEqualTo("0799999999");
        }

        @Test
        void setActive_changesActiveStatus() {
            Barber barber = BarberFixtures.persisted();

            barber.setActive(false);

            assertThat(barber.isActive()).isFalse();
        }
    }
}