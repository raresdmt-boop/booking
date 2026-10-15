
package com.barbershop.booking.appointments.models;

import com.barbershop.booking.procedures.models.Procedure;
import com.barbershop.booking.support.AppointmentFixtures;
import com.barbershop.booking.support.AppointmentProcedureFixtures;
import com.barbershop.booking.support.BarberFixtures;
import com.barbershop.booking.support.CustomerFixtures;
import com.barbershop.booking.support.ProcedureFixtures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AppointmentProcedure - unit tests")
class AppointmentProcedureTest {

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("Constructor should initialize all fields")
        void constructor_initializesFields() {

            // Arrange
            Appointment appointment = AppointmentFixtures.persisted(
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );

            Procedure procedure = ProcedureFixtures.persisted();
            // Act
            AppointmentProcedure appointmentProcedure =
                    AppointmentProcedureFixtures.appointmentProcedure(
                            appointment, procedure
                    );

            // Assert
            assertThat(appointmentProcedure.getId()).isNull();
            assertThat(appointmentProcedure.getAppointment())
                    .isSameAs(appointment);
            assertThat(appointmentProcedure.getProcedure())
                    .isSameAs(procedure);
            assertThat(appointmentProcedure.getPrice())
                    .isEqualByComparingTo(new BigDecimal("50.00"));
            assertThat(appointmentProcedure.getDurationMinutes())
                    .isEqualTo(30);
        }
    }

    @Nested
    @DisplayName("Equals tests")
    class EqualsTests {

        @Test
        @DisplayName("Same instance should be equal")
        void equals_sameInstance_returnsTrue() {

            // Arrange
            AppointmentProcedure ap =
                    AppointmentProcedureFixtures.persisted(null, null);

            // Act
            boolean result = ap.equals(ap);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Different instances with null IDs should not be equal")
        void equals_nullIds_returnsFalse() {

            // Arrange
            AppointmentProcedure ap1 =
                    AppointmentProcedureFixtures.appointmentProcedure(null, null);

            AppointmentProcedure ap2 =
                    AppointmentProcedureFixtures.appointmentProcedure(null, null);

            // Act
            boolean result = ap1.equals(ap2);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Same IDs should be equal")
        void equals_sameId_returnsTrue() {

            // Arrange
            AppointmentProcedure ap1 =
                    AppointmentProcedureFixtures.persisted(null, null);

            AppointmentProcedure ap2 =
                    AppointmentProcedureFixtures.persisted(null, null);

            // Act
            boolean result = ap1.equals(ap2);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Different IDs should not be equal")
        void equals_differentIds_returnsFalse() {

            // Arrange
            AppointmentProcedure ap1 =
                    AppointmentProcedureFixtures.persisted(null, null);

            AppointmentProcedure ap2 =
                    AppointmentProcedureFixtures.withId(
                            AppointmentProcedureFixtures.OTHER_ID,
                            null,
                            null
                    );

            // Act
            boolean result = ap1.equals(ap2);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Comparison with null should return false")
        void equals_null_returnsFalse() {

            // Arrange
            AppointmentProcedure ap =
                    AppointmentProcedureFixtures.persisted(null, null);

            // Act
            boolean result = ap.equals(null);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Comparison with another object type should return false")
        void equals_differentType_returnsFalse() {

            // Arrange
            AppointmentProcedure ap =
                    AppointmentProcedureFixtures.persisted(null, null);

            String otherObject = "test";

            // Act
            boolean result = ap.equals(otherObject);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("One null ID should return false")
        void equals_oneNullId_returnsFalse() {

            // Arrange
            AppointmentProcedure ap1 =
                    AppointmentProcedureFixtures.persisted(null, null);

            AppointmentProcedure ap2 =
                    AppointmentProcedureFixtures.appointmentProcedure(null, null);

            // Act
            boolean result = ap1.equals(ap2);

            // Assert
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("HashCode tests")
    class HashCodeTests {

        @Test
        @DisplayName("Same IDs should have the same hashCode")
        void hashCode_sameId_returnsSameHashCode() {

            // Arrange
            AppointmentProcedure ap1 =
                    AppointmentProcedureFixtures.persisted(null, null);

            AppointmentProcedure ap2 =
                    AppointmentProcedureFixtures.persisted(null, null);

            // Act
            int hash1 = ap1.hashCode();
            int hash2 = ap2.hashCode();

            // Assert
            assertThat(hash1).isEqualTo(hash2);
        }

        @Test
        @DisplayName("Different IDs should have the same hashCode")
        void hashCode_differentIds_returnsSameHashCode() {

            // Arrange
            AppointmentProcedure ap1 =
                    AppointmentProcedureFixtures.persisted(null, null);

            AppointmentProcedure ap2 =
                    AppointmentProcedureFixtures.withId(
                            AppointmentProcedureFixtures.OTHER_ID,
                            null,
                            null
                    );

            // Act
            int hash1 = ap1.hashCode();
            int hash2 = ap2.hashCode();

            // Assert
            assertThat(hash1).isEqualTo(hash2);
        }
    }

    @Nested
    @DisplayName("ToString tests")
    class ToStringTests {

        @Test
        @DisplayName("toString should contain appointment procedure information")
        void toString_returnsAppointmentProcedureInformation() {

            // Arrange
            AppointmentProcedure ap =
                    AppointmentProcedureFixtures.persisted(null, null);

            // Act
            String result = ap.toString();

            // Assert
            assertThat(result)
                    .contains("id=1")
                    .contains("price=50.00")
                    .contains("durationMinutes=30");
        }
    }

    @Nested
    @DisplayName("Setter tests")
    class SetterTests {

        @Test
        @DisplayName("Price should be updated")
        void setPrice_updatesPrice() {

            // Arrange
            AppointmentProcedure ap =
                    AppointmentProcedureFixtures.appointmentProcedure(null, null);

            // Act
            ap.setPrice(new BigDecimal("70.00"));

            // Assert
            assertThat(ap.getPrice())
                    .isEqualByComparingTo(new BigDecimal("70.00"));
        }

        @Test
        @DisplayName("Duration should be updated")
        void setDurationMinutes_updatesDuration() {

            // Arrange
            AppointmentProcedure ap =
                    AppointmentProcedureFixtures.appointmentProcedure(null, null);

            // Act
            ap.setDurationMinutes(45);

            // Assert
            assertThat(ap.getDurationMinutes()).isEqualTo(45);
        }
    }
}
