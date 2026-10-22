package com.barbershop.booking.customers.models;

import com.barbershop.booking.support.CustomerFixtures;
import com.barbershop.booking.support.UserFixtures;
import com.barbershop.booking.users.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        void constructor_initializesFields() {
            User user = UserFixtures.persisted();

            LocalDateTime before = LocalDateTime.now();

            Customer customer = new Customer(
                    user,
                    "Ion",
                    "Popescu",
                    "0712345678"
            );

            LocalDateTime after = LocalDateTime.now();

            assertThat(customer.getId()).isNull();
            assertThat(customer.getUser()).isSameAs(user);
            assertThat(customer.getFirstName()).isEqualTo("Ion");
            assertThat(customer.getLastName()).isEqualTo("Popescu");
            assertThat(customer.getPhone()).isEqualTo("0712345678");
            assertThat(customer.isBlocked()).isFalse();
            assertThat(customer.getCreatedAt()).isBetween(before, after);
            assertThat(customer.getAppointments()).isEmpty();
            assertThat(customer.getRecurringAppointments()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Equals")
    class EqualsTests {

        @Test
        void equals_sameInstance_returnsTrue() {
            Customer customer = CustomerFixtures.persisted();

            assertThat(customer).isEqualTo(customer);
        }

        @Test
        void equals_sameId_returnsTrue() {
            Customer first = CustomerFixtures.withId(1L);
            Customer second = CustomerFixtures.withId(1L);

            assertThat(first).isEqualTo(second);
        }

        @Test
        void equals_differentIds_returnsFalse() {
            Customer first = CustomerFixtures.withId(1L);
            Customer second = CustomerFixtures.withId(2L);

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_bothIdsNull_returnsFalse() {
            Customer first = CustomerFixtures.customer();
            Customer second = CustomerFixtures.customer();

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_oneIdNull_returnsFalse() {
            Customer first = CustomerFixtures.persisted();
            Customer second = CustomerFixtures.customer();

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_null_returnsFalse() {
            Customer customer = CustomerFixtures.persisted();

            assertThat(customer).isNotEqualTo(null);
        }

        @Test
        void equals_differentClass_returnsFalse() {
            Customer customer = CustomerFixtures.persisted();

            assertThat(customer).isNotEqualTo("text");
        }
    }

    @Nested
    @DisplayName("HashCode")
    class HashCodeTests {

        @Test
        void hashCode_sameId_returnsSameHashCode() {
            Customer first = CustomerFixtures.withId(1L);
            Customer second = CustomerFixtures.withId(1L);

            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }

        @Test
        void hashCode_differentIds_returnsSameHashCode() {
            Customer first = CustomerFixtures.withId(1L);
            Customer second = CustomerFixtures.withId(2L);

            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToStringTests {

        @Test
        void toString_containsRelevantFields() {
            Customer customer = new Customer(
                    UserFixtures.persisted(),
                    "Ion",
                    "Popescu",
                    "0712345678"
            );

            String result = customer.toString();

            assertThat(result)
                    .contains("firstName='Ion'")
                    .contains("lastName='Popescu'")
                    .contains("phone='0712345678'")
                    .contains("blocked=false")
                    .contains("userId=1");
        }
    }

    @Nested
    @DisplayName("Setters")
    class SetterTests {

        @Test
        void setFirstName_changesFirstName() {
            Customer customer = CustomerFixtures.persisted();

            customer.setFirstName("Andrei");

            assertThat(customer.getFirstName()).isEqualTo("Andrei");
        }

        @Test
        void setLastName_changesLastName() {
            Customer customer = CustomerFixtures.persisted();

            customer.setLastName("Ionescu");

            assertThat(customer.getLastName()).isEqualTo("Ionescu");
        }

        @Test
        void setPhone_changesPhone() {
            Customer customer = CustomerFixtures.persisted();

            customer.setPhone("0799999999");

            assertThat(customer.getPhone()).isEqualTo("0799999999");
        }

        @Test
        void setBlocked_changesBlockedStatus() {
            Customer customer = CustomerFixtures.persisted();

            customer.setBlocked(true);

            assertThat(customer.isBlocked()).isTrue();
        }
    }
}