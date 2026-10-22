package com.barbershop.booking.appointments.models;

import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.customers.models.Customer;
import com.barbershop.booking.support.BarberFixtures;
import com.barbershop.booking.support.CustomerFixtures;
import com.barbershop.booking.support.RecurringAppointmentFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class RecurringAppointmentTest {

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        void constructor_initializesFields() {
            // Arrange
            Customer customer = CustomerFixtures.persisted();
            Barber barber = BarberFixtures.persisted();

            LocalDate startDate = LocalDate.of(2026, 10, 20);
            LocalDate endDate = LocalDate.of(2026, 12, 20);
            LocalTime startTime = LocalTime.of(10, 0);

            // Act
            RecurringAppointment recurringAppointment =
                    new RecurringAppointment(
                            customer,
                            2,
                            startDate,
                            endDate,
                            startTime,
                            barber
                    );

            // Assert
            assertThat(recurringAppointment.getId()).isNull();
            assertThat(recurringAppointment.getCustomer()).isSameAs(customer);
            assertThat(recurringAppointment.getBarber()).isSameAs(barber);
            assertThat(recurringAppointment.getIntervalWeeks()).isEqualTo(2);
            assertThat(recurringAppointment.getStartDate()).isEqualTo(startDate);
            assertThat(recurringAppointment.getEndDate()).isEqualTo(endDate);
            assertThat(recurringAppointment.getStartTime()).isEqualTo(startTime);
            assertThat(recurringAppointment.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("Equals")
    class EqualsTests {

        @Test
        void equals_sameInstance_returnsTrue() {
            // Arrange
            RecurringAppointment recurringAppointment =
                    RecurringAppointmentFixtures.persisted();

            // Act & Assert
            assertThat(recurringAppointment)
                    .isEqualTo(recurringAppointment);
        }

        @Test
        void equals_sameId_returnsTrue() {
            // Arrange
            RecurringAppointment first =
                    RecurringAppointmentFixtures.withId(1L);

            RecurringAppointment second =
                    RecurringAppointmentFixtures.withId(1L);

            // Act & Assert
            assertThat(first).isEqualTo(second);
        }

        @Test
        void equals_differentIds_returnsFalse() {
            // Arrange
            RecurringAppointment first =
                    RecurringAppointmentFixtures.withId(1L);

            RecurringAppointment second =
                    RecurringAppointmentFixtures.withId(2L);

            // Act & Assert
            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_bothIdsNull_returnsFalse() {
            // Arrange
            RecurringAppointment first =
                    RecurringAppointmentFixtures.recurringAppointment();

            RecurringAppointment second =
                    RecurringAppointmentFixtures.recurringAppointment();

            // Act & Assert
            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_oneIdNull_returnsFalse() {
            // Arrange
            RecurringAppointment first =
                    RecurringAppointmentFixtures.persisted();

            RecurringAppointment second =
                    RecurringAppointmentFixtures.recurringAppointment();

            // Act & Assert
            assertThat(first).isNotEqualTo(second);
        }

        @Test
        void equals_null_returnsFalse() {
            // Arrange
            RecurringAppointment recurringAppointment =
                    RecurringAppointmentFixtures.persisted();

            // Act & Assert
            assertThat(recurringAppointment).isNotEqualTo(null);
        }

        @Test
        void equals_differentClass_returnsFalse() {
            // Arrange
            RecurringAppointment recurringAppointment =
                    RecurringAppointmentFixtures.persisted();

            // Act & Assert
            assertThat(recurringAppointment).isNotEqualTo("text");
        }
    }

    @Nested
    @DisplayName("HashCode")
    class HashCodeTests {

        @Test
        void hashCode_sameId_returnsSameHashCode() {
            // Arrange
            RecurringAppointment first =
                    RecurringAppointmentFixtures.withId(1L);

            RecurringAppointment second =
                    RecurringAppointmentFixtures.withId(1L);

            // Act & Assert
            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }

        @Test
        void hashCode_differentIds_returnsSameHashCode() {
            // Arrange
            RecurringAppointment first =
                    RecurringAppointmentFixtures.withId(1L);

            RecurringAppointment second =
                    RecurringAppointmentFixtures.withId(2L);

            // Act & Assert
            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToStringTests {

        @Test
        void toString_containsRelevantFields() {
            // Arrange
            RecurringAppointment recurringAppointment =
                    RecurringAppointmentFixtures.persisted();

            // Act
            String result = recurringAppointment.toString();

            // Assert
            assertThat(result)
                    .contains("id=1")
                    .contains("intervalWeeks=2")
                    .contains("startDate=2026-10-20")
                    .contains("endDate=2026-12-20")
                    .contains("active=true")
                    .contains("startTime=10:00");
        }
    }

    @Nested
    @DisplayName("Setters")
    class SetterTests {

        @Test
        void setActive_changesActiveStatus() {
            // Arrange
            RecurringAppointment recurringAppointment =
                    RecurringAppointmentFixtures.persisted();

            // Act
            recurringAppointment.setActive(false);

            // Assert
            assertThat(recurringAppointment.isActive()).isFalse();
        }

        @Test
        void setIntervalWeeks_changesInterval() {
            // Arrange
            RecurringAppointment recurringAppointment =
                    RecurringAppointmentFixtures.persisted();

            // Act
            recurringAppointment.setIntervalWeeks(4);

            // Assert
            assertThat(recurringAppointment.getIntervalWeeks()).isEqualTo(4);
        }

        @Test
        void setEndDate_changesEndDate() {
            // Arrange
            RecurringAppointment recurringAppointment =
                    RecurringAppointmentFixtures.persisted();

            LocalDate newEndDate = LocalDate.of(2027, 1, 20);

            // Act
            recurringAppointment.setEndDate(newEndDate);

            // Assert
            assertThat(recurringAppointment.getEndDate()).isEqualTo(newEndDate);
        }
    }
}