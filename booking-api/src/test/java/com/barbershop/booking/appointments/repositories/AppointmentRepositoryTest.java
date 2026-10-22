
package com.barbershop.booking.appointments.repositories;

import com.barbershop.booking.appointments.models.Appointment;
import com.barbershop.booking.appointments.models.AppointmentStatus;
import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.customers.models.Customer;
import com.barbershop.booking.support.AppointmentFixtures;
import com.barbershop.booking.support.BarberFixtures;
import com.barbershop.booking.support.CustomerFixtures;
import com.barbershop.booking.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@DisplayName("AppointmentRepository tests")
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer customer;
    private Barber barber;

    private final LocalDateTime startTime =
            LocalDateTime.of(2026, 10, 20, 10, 0);

    private final LocalDateTime endTime =
            LocalDateTime.of(2026, 10, 20, 10, 30);

    @BeforeEach
    void setUp() {
        Customer customerFixture = CustomerFixtures.customer();
        Barber barberFixture = BarberFixtures.barber();

        entityManager.persist(customerFixture.getUser());
        customer = entityManager.persist(customerFixture);

        entityManager.persist(barberFixture.getUser());
        barber = entityManager.persistAndFlush(barberFixture);

        entityManager.clear();
    }

    private Appointment createAppointment() {
        return AppointmentFixtures.appointment(customer, barber);
    }

    private Appointment createAppointment(
            LocalDateTime start,
            LocalDateTime end) {

        return new Appointment(
                customer,
                barber,
                start,
                end,
                "Test appointment"
        );
    }

    @Nested
    @DisplayName("Save appointment")
    class SaveAppointment {

        @Test
        @DisplayName("Should save appointment successfully")
        void shouldSaveAppointmentSuccessfully() {
            Appointment appointment = createAppointment();

            Appointment saved = appointmentRepository.saveAndFlush(appointment);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getCustomer().getId()).isEqualTo(customer.getId());
            assertThat(saved.getBarber().getId()).isEqualTo(barber.getId());
            assertThat(saved.getStatus()).isEqualTo(AppointmentStatus.BOOKED);
            assertThat(saved.getStartTime()).isEqualTo(startTime);
            assertThat(saved.getEndTime()).isEqualTo(endTime);
            assertThat(saved.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Find appointment by ID")
    class FindAppointmentById {

        @Test
        @DisplayName("Should return appointment when ID exists")
        void shouldReturnAppointmentWhenIdExists() {
            Appointment saved = entityManager.persistAndFlush(createAppointment());
            entityManager.clear();

            Optional<Appointment> result =
                    appointmentRepository.findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getStatus())
                    .isEqualTo(AppointmentStatus.BOOKED);
        }

        @Test
        @DisplayName("Should return empty when ID does not exist")
        void shouldReturnEmptyWhenIdDoesNotExist() {
            Optional<Appointment> result =
                    appointmentRepository.findById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Find appointments by customer ID")
    class FindByCustomerId {

        @Test
        @DisplayName("Should return customer's appointments")
        void shouldReturnCustomerAppointments() {
            entityManager.persist(createAppointment());

            entityManager.persistAndFlush(
                    createAppointment(
                            startTime.plusHours(2),
                            endTime.plusHours(2)
                    )
            );

            entityManager.clear();

            List<Appointment> result =
                    appointmentRepository.findByCustomerId(customer.getId());

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Should return empty list when customer has no appointments")
        void shouldReturnEmptyListWhenCustomerHasNoAppointments() {
            List<Appointment> result =
                    appointmentRepository.findByCustomerId(customer.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Find appointments by barber ID")
    class FindByBarberId {

        @Test
        @DisplayName("Should return barber's appointments")
        void shouldReturnBarberAppointments() {
            entityManager.persist(createAppointment());

            entityManager.persistAndFlush(
                    createAppointment(
                            startTime.plusHours(2),
                            endTime.plusHours(2)
                    )
            );

            entityManager.clear();

            List<Appointment> result =
                    appointmentRepository.findByBarberId(barber.getId());

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Should return empty list when barber has no appointments")
        void shouldReturnEmptyListWhenBarberHasNoAppointments() {
            List<Appointment> result =
                    appointmentRepository.findByBarberId(barber.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Find appointments by barber and period")
    class FindByBarberAndPeriod {

        @Test
        @DisplayName("Should return appointments starting within period")
        void shouldReturnAppointmentsWithinPeriod() {
            entityManager.persist(createAppointment());

            entityManager.persistAndFlush(
                    createAppointment(
                            startTime.plusDays(2),
                            endTime.plusDays(2)
                    )
            );

            entityManager.clear();

            List<Appointment> result =
                    appointmentRepository.findByBarberIdAndStartTimeBetween(
                            barber.getId(),
                            startTime.minusHours(1),
                            endTime.plusHours(1)
                    );

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStartTime()).isEqualTo(startTime);
        }

        @Test
        @DisplayName("Should return empty list when period has no appointments")
        void shouldReturnEmptyListWhenPeriodHasNoAppointments() {
            entityManager.persistAndFlush(createAppointment());

            List<Appointment> result =
                    appointmentRepository.findByBarberIdAndStartTimeBetween(
                            barber.getId(),
                            startTime.plusDays(1),
                            endTime.plusDays(1)
                    );

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Overlapping appointments")
    class OverlappingAppointments {

        @Test
        @DisplayName("Should detect overlapping booked appointment")
        void shouldDetectOverlappingBookedAppointment() {
            entityManager.persistAndFlush(createAppointment());

            boolean result = appointmentRepository.hasOverlappingAppointmentExcludingId(
                    barber.getId(),
                    AppointmentStatus.CANCELED,
                    startTime.plusMinutes(45),
                    startTime.plusMinutes(15)
            );

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should not detect overlap when appointments are adjacent")
        void shouldNotDetectOverlapWhenAppointmentsAreAdjacent() {
            entityManager.persistAndFlush(createAppointment());

            boolean result = appointmentRepository.hasOverlappingAppointmentExcludingId(
                    barber.getId(),
                    AppointmentStatus.CANCELED,
                    endTime.plusMinutes(30),
                    endTime
            );

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should ignore canceled appointments")
        void shouldIgnoreCanceledAppointments() {
            Appointment appointment = createAppointment();
            appointment.setStatus(AppointmentStatus.CANCELED);

            entityManager.persistAndFlush(appointment);

            boolean result = appointmentRepository.hasOverlappingAppointmentExcludingId(
                    barber.getId(),
                    AppointmentStatus.CANCELED,
                    endTime,
                    startTime
            );

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should detect overlapping confirmed appointment")
        void shouldDetectOverlappingConfirmedAppointment() {
            Appointment appointment = createAppointment();
            appointment.setStatus(AppointmentStatus.CONFIRMED);

            entityManager.persistAndFlush(appointment);

            boolean result = appointmentRepository.hasOverlappingAppointmentExcludingId(
                    barber.getId(),
                    AppointmentStatus.CANCELED,
                    endTime,
                    startTime
            );

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should ignore current appointment when updating")
        void shouldIgnoreCurrentAppointmentWhenUpdating() {
            Appointment saved = entityManager.persistAndFlush(createAppointment());

            boolean result =
                    appointmentRepository.hasOverlappingAppointment(
                            barber.getId(),
                            saved.getId(),
                            AppointmentStatus.CANCELED,
                            endTime,
                            startTime
                    );

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should detect another overlapping appointment when updating")
        void shouldDetectAnotherOverlappingAppointmentWhenUpdating() {
            Appointment first = entityManager.persistAndFlush(createAppointment());

            Appointment second = entityManager.persistAndFlush(
                    createAppointment(
                            startTime.plusHours(2),
                            endTime.plusHours(2)
                    )
            );

            boolean result =
                    appointmentRepository.hasOverlappingAppointment(
                            barber.getId(),
                            second.getId(),
                            AppointmentStatus.CANCELED,
                            endTime,
                            startTime
                    );

            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("Delete appointment")
    class DeleteAppointment {

        @Test
        @DisplayName("Should delete appointment successfully")
        void shouldDeleteAppointmentSuccessfully() {
            Appointment saved = entityManager.persistAndFlush(createAppointment());
            Long id = saved.getId();

            appointmentRepository.delete(saved);
            appointmentRepository.flush();
            entityManager.clear();

            assertThat(appointmentRepository.findById(id)).isEmpty();
        }
    }
}
