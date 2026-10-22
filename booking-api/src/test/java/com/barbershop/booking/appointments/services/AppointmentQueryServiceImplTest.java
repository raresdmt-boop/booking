
package com.barbershop.booking.appointments.services;

import com.barbershop.booking.appointments.dtos.appointment.AppointmentResponse;
import com.barbershop.booking.appointments.exceptions.AppointmentNotFound;
import com.barbershop.booking.appointments.models.Appointment;
import com.barbershop.booking.appointments.models.AppointmentStatus;
import com.barbershop.booking.appointments.repositories.AppointmentProcedureRepository;
import com.barbershop.booking.appointments.repositories.AppointmentRepository;
import com.barbershop.booking.appointments.services.appointment.AppointmentQueryServiceImpl;
import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.customers.models.Customer;
import com.barbershop.booking.support.AppointmentFixtures;
import com.barbershop.booking.support.BarberFixtures;
import com.barbershop.booking.support.CustomerFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.barbershop.booking.appointments.models.AppointmentProcedure;
import com.barbershop.booking.procedures.models.Procedure;
import com.barbershop.booking.support.AppointmentProcedureFixtures;
import com.barbershop.booking.support.ProcedureFixtures;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentQueryServiceImpl tests")
class AppointmentQueryServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentProcedureRepository appointmentProcedureRepository;

    @InjectMocks
    private AppointmentQueryServiceImpl queryService;

    private Customer customer;
    private Barber barber;
    private Appointment appointment;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        customer = CustomerFixtures.persisted();
        barber = BarberFixtures.persisted();
        appointment = AppointmentFixtures.persisted(customer, barber);

        startTime = LocalDateTime.of(2026, 10, 20, 10, 0);
        endTime = LocalDateTime.of(2026, 10, 20, 10, 30);
    }

    @Nested
    @DisplayName("Get appointment by ID")
    class GetAppointmentById {

        @Test
        @DisplayName("Should return appointment when ID exists")
        void shouldReturnAppointmentWhenIdExists() {
            when(appointmentRepository.findById(AppointmentFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(appointment));

            AppointmentResponse response =
                    queryService.getAppointmentById(AppointmentFixtures.KNOWN_ID);

            assertThat(response.id()).isEqualTo(AppointmentFixtures.KNOWN_ID);
            assertThat(response.customerId()).isEqualTo(customer.getId());
            assertThat(response.barberId()).isEqualTo(barber.getId());
            assertThat(response.status()).isEqualTo(AppointmentStatus.BOOKED);
        }

        @Test
        @DisplayName("Should throw exception when appointment does not exist")
        void shouldThrowExceptionWhenAppointmentDoesNotExist() {
            when(appointmentRepository.findById(AppointmentFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    queryService.getAppointmentById(AppointmentFixtures.OTHER_ID)
            ).isInstanceOf(AppointmentNotFound.class);
        }
    }
    @Test
    @DisplayName("Should return appointment with procedures")
    void shouldReturnAppointmentWithProcedures() {

        Procedure procedure = ProcedureFixtures.withId(1L);

        AppointmentProcedure appointmentProcedure =
                AppointmentProcedureFixtures.persisted(appointment, procedure);

        when(appointmentRepository.findById(AppointmentFixtures.KNOWN_ID))
                .thenReturn(Optional.of(appointment));

        when(appointmentProcedureRepository.findByAppointmentId(AppointmentFixtures.KNOWN_ID))
                .thenReturn(List.of(appointmentProcedure));

        AppointmentResponse response =
                queryService.getAppointmentById(AppointmentFixtures.KNOWN_ID);

        assertThat(response.procedures()).hasSize(1);

        assertThat(response.procedures().get(0).procedureId())
                .isEqualTo(1L);

        assertThat(response.procedures().get(0).price())
                .isEqualByComparingTo("50.00");

        assertThat(response.procedures().get(0).durationMinutes())
                .isEqualTo(30);
    }

    @Nested
    @DisplayName("Get all appointments")
    class GetAppointments {

        @Test
        @DisplayName("Should return all appointments")
        void shouldReturnAllAppointments() {
            Appointment secondAppointment =
                    AppointmentFixtures.withId(
                            AppointmentFixtures.OTHER_ID,
                            customer,
                            barber
                    );

            when(appointmentRepository.findAll())
                    .thenReturn(List.of(appointment, secondAppointment));

            List<AppointmentResponse> responses =
                    queryService.getAppointments();

            assertThat(responses).hasSize(2);
            assertThat(responses)
                    .extracting(AppointmentResponse::id)
                    .containsExactly(
                            AppointmentFixtures.KNOWN_ID,
                            AppointmentFixtures.OTHER_ID
                    );
        }

        @Test
        @DisplayName("Should return each appointment with its own procedures")
        void shouldReturnEachAppointmentWithItsOwnProcedures() {

            Appointment secondAppointment = AppointmentFixtures.withId(
                    AppointmentFixtures.OTHER_ID,
                    customer,
                    barber
            );

            Procedure haircut = ProcedureFixtures.withId(1L);
            Procedure beard = ProcedureFixtures.withId(2L);

            AppointmentProcedure firstProcedure =
                    AppointmentProcedureFixtures.withId(1L, appointment, haircut);

            AppointmentProcedure secondProcedure =
                    AppointmentProcedureFixtures.withId(2L, secondAppointment, beard);

            when(appointmentRepository.findAll())
                    .thenReturn(List.of(appointment, secondAppointment));

            when(appointmentProcedureRepository.findByAppointmentId(AppointmentFixtures.KNOWN_ID))
                    .thenReturn(List.of(firstProcedure));

            when(appointmentProcedureRepository.findByAppointmentId(AppointmentFixtures.OTHER_ID))
                    .thenReturn(List.of(secondProcedure));

            List<AppointmentResponse> responses = queryService.getAppointments();

            assertThat(responses).hasSize(2);

            assertThat(responses.get(0).procedures()).hasSize(1);
            assertThat(responses.get(0).procedures().get(0).procedureId())
                    .isEqualTo(1L);

            assertThat(responses.get(1).procedures()).hasSize(1);
            assertThat(responses.get(1).procedures().get(0).procedureId())
                    .isEqualTo(2L);
        }

        @Test
        @DisplayName("Should return empty list when no appointments exist")
        void shouldReturnEmptyListWhenNoAppointmentsExist() {
            when(appointmentRepository.findAll())
                    .thenReturn(List.of());

            List<AppointmentResponse> responses =
                    queryService.getAppointments();

            assertThat(responses).isEmpty();
        }
    }


    @Nested
    @DisplayName("Get appointments by customer ID")
    class GetAppointmentsByCustomerId {

        @Test
        @DisplayName("Should return customer's appointments")
        void shouldReturnCustomerAppointments() {
            when(appointmentRepository.findByCustomerId(customer.getId()))
                    .thenReturn(List.of(appointment));

            List<AppointmentResponse> responses =
                    queryService.getAppointmentsByCustomerId(customer.getId());

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).customerId())
                    .isEqualTo(customer.getId());
        }

        @Test
        @DisplayName("Should return customer appointments with procedures")
        void shouldReturnCustomerAppointmentsWithProcedures() {

            Procedure haircut = ProcedureFixtures.withId(1L);

            AppointmentProcedure appointmentProcedure =
                    AppointmentProcedureFixtures.persisted(appointment, haircut);

            when(appointmentRepository.findByCustomerId(customer.getId()))
                    .thenReturn(List.of(appointment));

            when(appointmentProcedureRepository.findByAppointmentId(appointment.getId()))
                    .thenReturn(List.of(appointmentProcedure));

            List<AppointmentResponse> responses =
                    queryService.getAppointmentsByCustomerId(customer.getId());

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).customerId()).isEqualTo(customer.getId());

            assertThat(responses.get(0).procedures()).hasSize(1);
            assertThat(responses.get(0).procedures().get(0).procedureId())
                    .isEqualTo(1L);
        }

        @Test
        @DisplayName("Should return empty list when customer has no appointments")
        void shouldReturnEmptyListWhenCustomerHasNoAppointments() {
            when(appointmentRepository.findByCustomerId(customer.getId()))
                    .thenReturn(List.of());

            List<AppointmentResponse> responses =
                    queryService.getAppointmentsByCustomerId(customer.getId());

            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("Get appointments by barber ID")
    class GetAppointmentsByBarberId {

        @Test
        @DisplayName("Should return barber's appointments")
        void shouldReturnBarberAppointments() {
            when(appointmentRepository.findByBarberId(barber.getId()))
                    .thenReturn(List.of(appointment));

            List<AppointmentResponse> responses =
                    queryService.getAppointmentsByBarberId(barber.getId());

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).barberId())
                    .isEqualTo(barber.getId());
        }

        @Test
        @DisplayName("Should return barber appointments with procedures")
        void shouldReturnBarberAppointmentsWithProcedures() {

            Procedure haircut = ProcedureFixtures.withId(1L);

            AppointmentProcedure appointmentProcedure =
                    AppointmentProcedureFixtures.persisted(appointment, haircut);

            when(appointmentRepository.findByBarberId(barber.getId()))
                    .thenReturn(List.of(appointment));

            when(appointmentProcedureRepository.findByAppointmentId(appointment.getId()))
                    .thenReturn(List.of(appointmentProcedure));

            List<AppointmentResponse> responses =
                    queryService.getAppointmentsByBarberId(barber.getId());

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).barberId()).isEqualTo(barber.getId());

            assertThat(responses.get(0).procedures()).hasSize(1);
            assertThat(responses.get(0).procedures().get(0).procedureId())
                    .isEqualTo(1L);
        }

        @Test
        @DisplayName("Should return empty list when barber has no appointments")
        void shouldReturnEmptyListWhenBarberHasNoAppointments() {
            when(appointmentRepository.findByBarberId(barber.getId()))
                    .thenReturn(List.of());

            List<AppointmentResponse> responses =
                    queryService.getAppointmentsByBarberId(barber.getId());

            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("Get appointments by barber and period")
    class GetAppointmentsByBarberIdAndPeriod {

        @Test
        @DisplayName("Should return appointments within period")
        void shouldReturnAppointmentsWithinPeriod() {
            LocalDateTime periodStart = startTime.minusHours(1);
            LocalDateTime periodEnd = endTime.plusHours(1);

            when(appointmentRepository.findByBarberIdAndStartTimeBetween(
                    barber.getId(),
                    periodStart,
                    periodEnd
            )).thenReturn(List.of(appointment));

            List<AppointmentResponse> responses =
                    queryService.getAppointmentsByBarberIdAndPeriod(
                            barber.getId(),
                            periodStart,
                            periodEnd
                    );

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).startTime()).isEqualTo(startTime);
            assertThat(responses.get(0).endTime()).isEqualTo(endTime);
        }

        @Test
        @DisplayName("Should return appointments within period with procedures")
        void shouldReturnAppointmentsWithinPeriodWithProcedures() {

            LocalDateTime periodStart = startTime.minusHours(1);
            LocalDateTime periodEnd = endTime.plusHours(1);

            Procedure haircut = ProcedureFixtures.withId(1L);

            AppointmentProcedure appointmentProcedure =
                    AppointmentProcedureFixtures.persisted(appointment, haircut);

            when(appointmentRepository.findByBarberIdAndStartTimeBetween(
                    barber.getId(),
                    periodStart,
                    periodEnd
            )).thenReturn(List.of(appointment));

            when(appointmentProcedureRepository.findByAppointmentId(appointment.getId()))
                    .thenReturn(List.of(appointmentProcedure));

            List<AppointmentResponse> responses =
                    queryService.getAppointmentsByBarberIdAndPeriod(
                            barber.getId(),
                            periodStart,
                            periodEnd
                    );

            assertThat(responses).hasSize(1);

            assertThat(responses.get(0).startTime()).isEqualTo(startTime);
            assertThat(responses.get(0).endTime()).isEqualTo(endTime);

            assertThat(responses.get(0).procedures()).hasSize(1);
            assertThat(responses.get(0).procedures().get(0).procedureId())
                    .isEqualTo(1L);
        }

        @Test
        @DisplayName("Should return empty list when no appointments exist in period")
        void shouldReturnEmptyListWhenNoAppointmentsExistInPeriod() {
            LocalDateTime periodStart = startTime.plusDays(1);
            LocalDateTime periodEnd = endTime.plusDays(1);

            when(appointmentRepository.findByBarberIdAndStartTimeBetween(
                    barber.getId(),
                    periodStart,
                    periodEnd
            )).thenReturn(List.of());

            List<AppointmentResponse> responses =
                    queryService.getAppointmentsByBarberIdAndPeriod(
                            barber.getId(),
                            periodStart,
                            periodEnd
                    );

            assertThat(responses).isEmpty();
        }
    }
}
