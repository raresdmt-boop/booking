package com.barbershop.booking.appointments.models;

import com.barbershop.booking.support.AppointmentFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.barbershop.booking.support.CustomerFixtures;
import com.barbershop.booking.support.BarberFixtures;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Appointment - unit tests")
class AppointmentTest {

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("Constructor should initialize appointment fields")
        void constructor_initializesFields() {

            // Arrange & Act
            Appointment appointment =
                    AppointmentFixtures.appointment(null, null);

            // Assert
            assertThat(appointment.getId()).isNull();
            assertThat(appointment.getCustomer()).isNull();
            assertThat(appointment.getBarber()).isNull();

            assertThat(appointment.getStartTime())
                    .isEqualTo(LocalDateTime.of(2026, 10, 20, 10, 0));

            assertThat(appointment.getEndTime())
                    .isEqualTo(LocalDateTime.of(2026, 10, 20, 10, 30));

            assertThat(appointment.getNotes())
                    .isEqualTo("Test appointment");

            assertThat(appointment.getCreatedAt()).isNotNull();

            assertThat(appointment.getStatus())
                    .isEqualTo(AppointmentStatus.BOOKED);

            assertThat(appointment.getProcedures()).isEmpty();
        }
    }
    @Nested
    @DisplayName("Equals tests")
    class EqualsTests {

        @Test
        @DisplayName("Same appointment instance should be equal")
        void equals_sameInstance_returnsTrue() {

            Appointment appointment = AppointmentFixtures.persisted(
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );

            boolean result = appointment.equals(appointment);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Different appointments with null IDs should not be equal")
        void equals_differentAppointmentsWithNullIds_returnsFalse() {

            Appointment appointment1 = AppointmentFixtures.appointment(
                    CustomerFixtures.customer(),
                    BarberFixtures.barber()
            );

            Appointment appointment2 = AppointmentFixtures.appointment(
                    CustomerFixtures.customer(),
                    BarberFixtures.barber()
            );

            boolean result = appointment1.equals(appointment2);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Appointments with the same ID should be equal")
        void equals_sameId_returnsTrue() {

            Appointment appointment1 = AppointmentFixtures.persisted(
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );

            Appointment appointment2 = AppointmentFixtures.persisted(
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );

            boolean result = appointment1.equals(appointment2);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Appointments with different IDs should not be equal")
        void equals_differentIds_returnsFalse() {

            Appointment appointment1 = AppointmentFixtures.persisted(
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );

            Appointment appointment2 = AppointmentFixtures.withId(
                    AppointmentFixtures.OTHER_ID,
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );

            boolean result = appointment1.equals(appointment2);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Appointment compared with null should not be equal")
        void equals_null_returnsFalse() {

            Appointment appointment = AppointmentFixtures.persisted(
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );

            boolean result = appointment.equals(null);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Appointment compared with another object type should not be equal")
        void equals_differentObjectType_returnsFalse() {

            Appointment appointment = AppointmentFixtures.persisted(
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );

            String otherObject = "test";

            boolean result = appointment.equals(otherObject);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Appointment with ID compared to appointment without ID should not be equal")
        void equals_oneNullId_returnsFalse() {

            Appointment appointment1 = AppointmentFixtures.persisted(
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );

            Appointment appointment2 = AppointmentFixtures.appointment(
                    CustomerFixtures.customer(),
                    BarberFixtures.barber()
            );

            boolean result = appointment1.equals(appointment2);

            assertThat(result).isFalse();
        }
    }
    @Nested
    @DisplayName("HashCode tests")
    class HashCodeTests {

        @Test
        @DisplayName("Appointments with the same ID should have the same hashCode")
        void hashCode_sameId_returnsSameHashCode() {

            Appointment appointment1 = AppointmentFixtures.persisted(
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );

            Appointment appointment2 = AppointmentFixtures.persisted(
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );

            int hash1 = appointment1.hashCode();
            int hash2 = appointment2.hashCode();

            assertThat(hash1).isEqualTo(hash2);
        }

        @Test
        @DisplayName("Appointments with different IDs should have the same hashCode")
        void hashCode_differentIds_returnsSameHashCode() {

            Appointment appointment1 = AppointmentFixtures.persisted(
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );

            Appointment appointment2 = AppointmentFixtures.withId(
                    AppointmentFixtures.OTHER_ID,
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );


            int hash1 = appointment1.hashCode();
            int hash2 = appointment2.hashCode();

            assertThat(hash1).isEqualTo(hash2);
        }
    }
    @Nested
    @DisplayName("ToString tests")
    class ToStringTests {

        @Test
        @DisplayName("toString should contain appointment information")
        void toString_returnsAppointmentInformation() {

            Appointment appointment = AppointmentFixtures.persisted(
                    CustomerFixtures.persisted(),
                    BarberFixtures.persisted()
            );

            String result = appointment.toString();

            assertThat(result)
                    .contains("id=1")
                    .contains("startTime=2026-10-20T10:00")
                    .contains("endTime=2026-10-20T10:30")
                    .contains("status=BOOKED")
                    .contains("notes='Test appointment'")
                    .contains("createdAt=");
        }
    }

    @Nested
    @DisplayName("Status tests")
    class StatusTests {
        @Test
        @DisplayName("Appointment status should be updated")
        void setStatus_updatesStatus() {

            Appointment appointment = AppointmentFixtures.appointment(
                    CustomerFixtures.customer(),
                    BarberFixtures.barber()
            );

            appointment.setStatus(AppointmentStatus.CONFIRMED);

            assertThat(appointment.getStatus())
                    .isEqualTo(AppointmentStatus.CONFIRMED);
        }
    }

}