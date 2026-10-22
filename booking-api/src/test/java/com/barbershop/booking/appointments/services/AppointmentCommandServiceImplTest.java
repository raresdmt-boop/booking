
package com.barbershop.booking.appointments.services;

import com.barbershop.booking.appointments.dtos.appointment.AppointmentCreateRequest;
import com.barbershop.booking.appointments.dtos.appointment.AppointmentResponse;
import com.barbershop.booking.appointments.exceptions.AppointmentInvalidTimeInterval;
import com.barbershop.booking.appointments.exceptions.AppointmentTimeSlotUnavailable;
import com.barbershop.booking.appointments.models.Appointment;
import com.barbershop.booking.appointments.models.AppointmentStatus;
import com.barbershop.booking.appointments.repositories.AppointmentRepository;
import com.barbershop.booking.appointments.services.appointment.AppointmentCommandServiceImpl;
import com.barbershop.booking.barbers.exceptions.BarberNotFound;
import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.barbers.repositories.BarberRepository;
import com.barbershop.booking.barbers.repositories.BarberTimeOffRepository;
import com.barbershop.booking.customers.exceptions.CustomerNotFound;
import com.barbershop.booking.customers.models.Customer;
import com.barbershop.booking.customers.repository.CustomerRepository;
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
import org.springframework.test.util.ReflectionTestUtils;
import com.barbershop.booking.appointments.dtos.appointment.AppointmentUpdateRequest;
import com.barbershop.booking.appointments.exceptions.AppointmentNotFound;
import com.barbershop.booking.appointments.exceptions.AppointmentInvalidStatus;
import com.barbershop.booking.support.AppointmentFixtures;
import com.barbershop.booking.appointments.exceptions.AppointmentCannotBeCanceled;
import com.barbershop.booking.appointments.repositories.AppointmentProcedureRepository;
import com.barbershop.booking.procedures.repository.ProcedureRepository;
import com.barbershop.booking.appointments.exceptions.*;
import com.barbershop.booking.appointments.models.AppointmentProcedure;
import com.barbershop.booking.procedures.models.Procedure;
import com.barbershop.booking.support.ProcedureFixtures;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;


import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import com.barbershop.booking.barbers.repositories.BarberScheduleRepository;
import com.barbershop.booking.barbers.models.BarberSchedule;
import java.time.LocalTime;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentCommandServiceImpl tests")
class AppointmentCommandServiceImplTest {

    @Mock
    private ProcedureRepository procedureRepository;

    @Mock
    private AppointmentProcedureRepository appointmentProcedureRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BarberRepository barberRepository;

    @Mock
    private BarberScheduleRepository barberScheduleRepository;

    @Mock
    private BarberTimeOffRepository barberTimeOffRepository;

    @InjectMocks
    private AppointmentCommandServiceImpl commandService;

    private Customer customer;
    private Barber barber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        customer = CustomerFixtures.persisted();
        barber = BarberFixtures.persisted();

        startTime = LocalDateTime.of(2026, 10, 20, 10, 0);
        endTime = LocalDateTime.of(2026, 10, 20, 10, 30);
    }


    @Nested
    @DisplayName("Add appointment")
    class AddAppointment {

        private AppointmentCreateRequest createRequest(List<Long> procedureIds) {
            return new AppointmentCreateRequest(
                    customer.getId(),
                    barber.getId(),
                    startTime,
                    "Test appointment",
                    procedureIds
            );
        }

        private void mockCustomerAndBarber() {
            when(customerRepository.findById(customer.getId()))
                    .thenReturn(Optional.of(customer));

            when(barberRepository.findById(barber.getId()))
                    .thenReturn(Optional.of(barber));
        }

        private Procedure createProcedure(Long id, int duration) {
            Procedure procedure = ProcedureFixtures.withId(id);
            ReflectionTestUtils.setField(procedure, "durationMinutes", duration);
            ReflectionTestUtils.setField(procedure, "barber", barber);
            return procedure;
        }

        private void mockBarberSchedule() {

            BarberSchedule schedule = new BarberSchedule(
                    barber,
                    startTime.getDayOfWeek().getValue(),
                    LocalTime.of(9, 0),
                    LocalTime.of(17, 0)
            );

            when(barberScheduleRepository.findByBarberIdAndDayOfWeek(
                    barber.getId(),
                    startTime.getDayOfWeek().getValue()
            )).thenReturn(Optional.of(schedule));
        }

        @Test
        @DisplayName("Should throw exception when appointment overlaps barber time off")
        void shouldThrowExceptionWhenAppointmentOverlapsBarberTimeOff() {

            startTime = startTime.withHour(13).withMinute(0);

            Procedure haircut = createProcedure(1L, 30);

            AppointmentCreateRequest request =
                    createRequest(List.of(1L));

            mockCustomerAndBarber();

            when(procedureRepository.findAllById(anyList()))
                    .thenReturn(List.of(haircut));

            mockBarberSchedule();

            when(barberTimeOffRepository.hasOverlappingTimeOff(
                    barber.getId(),
                    startTime,
                    startTime.plusMinutes(30)
            )).thenReturn(true);

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(AppointmentTimeSlotUnavailable.class);

            verify(appointmentRepository, never())
                    .save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should allow appointment when barber has no overlapping time off")
        void shouldAllowAppointmentWhenBarberHasNoOverlappingTimeOff() {

            Procedure haircut = createProcedure(1L, 30);

            AppointmentCreateRequest request =
                    createRequest(List.of(1L));

            mockCustomerAndBarber();

            when(procedureRepository.findAllById(anyList()))
                    .thenReturn(List.of(haircut));

            mockBarberSchedule();

            when(barberTimeOffRepository.hasOverlappingTimeOff(
                    barber.getId(),
                    startTime,
                    startTime.plusMinutes(30)
            )).thenReturn(false);

            when(appointmentRepository.save(any(Appointment.class)))
                    .thenAnswer(invocation -> {
                        Appointment appointment = invocation.getArgument(0);
                        ReflectionTestUtils.setField(appointment, "id", 1L);
                        return appointment;
                    });

            when(appointmentProcedureRepository.save(any(AppointmentProcedure.class)))
                    .thenAnswer(invocation -> {
                        AppointmentProcedure appointmentProcedure = invocation.getArgument(0);
                        ReflectionTestUtils.setField(appointmentProcedure, "id", 10L);
                        return appointmentProcedure;
                    });

            AppointmentResponse response = commandService.addAppointment(request);

            assertThat(response.startTime()).isEqualTo(startTime);
            assertThat(response.endTime()).isEqualTo(startTime.plusMinutes(30));

            verify(barberTimeOffRepository).hasOverlappingTimeOff(
                    barber.getId(),
                    startTime,
                    startTime.plusMinutes(30)
            );

            verify(appointmentRepository).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw exception when barber does not work that day")
        void shouldThrowExceptionWhenBarberDoesNotWorkThatDay() {

            Procedure haircut = createProcedure(1L, 30);

            AppointmentCreateRequest request =
                    createRequest(List.of(1L));

            mockCustomerAndBarber();

            when(procedureRepository.findAllById(anyList()))
                    .thenReturn(List.of(haircut));

            when(barberScheduleRepository.findByBarberIdAndDayOfWeek(
                    barber.getId(),
                    startTime.getDayOfWeek().getValue()
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(AppointmentTimeSlotUnavailable.class);

            verify(appointmentRepository, never())
                    .save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw exception when appointment starts before working hours")
        void shouldThrowExceptionWhenAppointmentStartsBeforeWorkingHours() {

            startTime = startTime.withHour(8).withMinute(30);

            Procedure haircut = createProcedure(1L, 30);

            AppointmentCreateRequest request =
                    createRequest(List.of(1L));

            mockCustomerAndBarber();

            when(procedureRepository.findAllById(anyList()))
                    .thenReturn(List.of(haircut));

            mockBarberSchedule();

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(AppointmentTimeSlotUnavailable.class);

            verify(appointmentRepository, never())
                    .save(any(Appointment.class));
        }


        @Test
        @DisplayName("Should throw exception when appointment ends after working hours")
        void shouldThrowExceptionWhenAppointmentEndsAfterWorkingHours() {

            startTime = startTime.withHour(16).withMinute(45);

            Procedure haircut = createProcedure(1L, 30);

            AppointmentCreateRequest request =
                    createRequest(List.of(1L));

            mockCustomerAndBarber();

            when(procedureRepository.findAllById(anyList()))
                    .thenReturn(List.of(haircut));

            mockBarberSchedule();

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(AppointmentTimeSlotUnavailable.class);

            verify(appointmentRepository, never())
                    .save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should allow appointment ending exactly at closing time")
        void shouldAllowAppointmentEndingExactlyAtClosingTime() {

            startTime = startTime.withHour(16).withMinute(30);

            Procedure haircut = createProcedure(1L, 30);

            AppointmentCreateRequest request =
                    createRequest(List.of(1L));

            mockCustomerAndBarber();

            when(procedureRepository.findAllById(anyList()))
                    .thenReturn(List.of(haircut));

            mockBarberSchedule();

            when(appointmentRepository.save(any(Appointment.class)))
                    .thenAnswer(invocation -> {
                        Appointment appointment = invocation.getArgument(0);
                        ReflectionTestUtils.setField(appointment, "id", 1L);
                        return appointment;
                    });

            when(appointmentProcedureRepository.save(any(AppointmentProcedure.class)))
                    .thenAnswer(invocation -> {
                        AppointmentProcedure appointmentProcedure = invocation.getArgument(0);
                        ReflectionTestUtils.setField(appointmentProcedure, "id", 10L);
                        return appointmentProcedure;
                    });

            AppointmentResponse response = commandService.addAppointment(request);

            assertThat(response.startTime()).isEqualTo(startTime);
            assertThat(response.endTime())
                    .isEqualTo(startTime.plusMinutes(30));

            verify(appointmentRepository).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should create appointment with two procedures successfully")
        void shouldCreateAppointmentSuccessfully() {

            Procedure haircut = createProcedure(1L, 30);
            Procedure beard = createProcedure(2L, 15);

            AppointmentCreateRequest request =
                    createRequest(List.of(1L, 2L));

            mockCustomerAndBarber();
            mockBarberSchedule();

            when(procedureRepository.findAllById(anyList()))
                    .thenReturn(List.of(haircut, beard));

            when(appointmentRepository.save(any(Appointment.class)))
                    .thenAnswer(invocation -> {
                        Appointment savedAppointment = invocation.getArgument(0);
                        ReflectionTestUtils.setField(savedAppointment, "id", 1L);
                        return savedAppointment;
                    });

            when(appointmentProcedureRepository.save(any(AppointmentProcedure.class)))
                    .thenAnswer(invocation -> {
                        AppointmentProcedure savedProcedure = invocation.getArgument(0);
                        ReflectionTestUtils.setField(savedProcedure, "id", 10L);
                        return savedProcedure;
                    });

            AppointmentResponse response = commandService.addAppointment(request);

            LocalDateTime expectedEndTime = startTime.plusMinutes(45);

            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.customerId()).isEqualTo(customer.getId());
            assertThat(response.barberId()).isEqualTo(barber.getId());
            assertThat(response.startTime()).isEqualTo(startTime);
            assertThat(response.endTime()).isEqualTo(expectedEndTime);
            assertThat(response.status()).isEqualTo(AppointmentStatus.BOOKED);
            assertThat(response.notes()).isEqualTo("Test appointment");

            assertThat(response.procedures()).hasSize(2);
            assertThat(response.procedures())
                    .extracting("procedureId")
                    .containsExactly(1L, 2L);

            verify(appointmentRepository).hasOverlappingAppointmentExcludingId(
                    barber.getId(),
                    AppointmentStatus.CANCELED,
                    expectedEndTime,
                    startTime
            );

            verify(appointmentRepository).save(any(Appointment.class));

            verify(appointmentProcedureRepository, times(2))
                    .save(any(AppointmentProcedure.class));
        }

        @Test
        @DisplayName("Should save procedure price and duration")
        void shouldSaveProcedurePriceAndDuration() {

            Procedure haircut = createProcedure(1L, 30);

            AppointmentCreateRequest request =
                    createRequest(List.of(1L));

            mockCustomerAndBarber();
            mockBarberSchedule();

            when(procedureRepository.findAllById(anyList()))
                    .thenReturn(List.of(haircut));

            when(appointmentRepository.save(any(Appointment.class)))
                    .thenAnswer(invocation -> {
                        Appointment savedAppointment = invocation.getArgument(0);
                        ReflectionTestUtils.setField(savedAppointment, "id", 1L);
                        return savedAppointment;
                    });

            when(appointmentProcedureRepository.save(any(AppointmentProcedure.class)))
                    .thenAnswer(invocation -> {
                        AppointmentProcedure savedProcedure = invocation.getArgument(0);
                        ReflectionTestUtils.setField(savedProcedure, "id", 10L);
                        return savedProcedure;
                    });

            AppointmentResponse response = commandService.addAppointment(request);

            org.mockito.ArgumentCaptor<AppointmentProcedure> captor =
                    org.mockito.ArgumentCaptor.forClass(AppointmentProcedure.class);

            verify(appointmentProcedureRepository).save(captor.capture());

            AppointmentProcedure savedProcedure = captor.getValue();

            assertThat(savedProcedure.getAppointment().getId()).isEqualTo(1L);
            assertThat(savedProcedure.getProcedure()).isSameAs(haircut);
            assertThat(savedProcedure.getPrice()).isEqualByComparingTo(haircut.getPrice());
            assertThat(savedProcedure.getDurationMinutes()).isEqualTo(30);

            assertThat(response.procedures()).hasSize(1);
            assertThat(response.procedures().get(0).id()).isEqualTo(10L);
            assertThat(response.procedures().get(0).procedureId()).isEqualTo(1L);
            assertThat(response.procedures().get(0).price())
                    .isEqualByComparingTo(haircut.getPrice());
            assertThat(response.procedures().get(0).durationMinutes()).isEqualTo(30);
        }

        @Test
        @DisplayName("Should throw exception when time slot is unavailable")
        void shouldThrowExceptionWhenTimeSlotIsUnavailable() {

            Procedure haircut = createProcedure(1L, 30);

            AppointmentCreateRequest request =
                    createRequest(List.of(1L));

            mockCustomerAndBarber();
            mockBarberSchedule();

            when(procedureRepository.findAllById(anyList()))
                    .thenReturn(List.of(haircut));

            when(appointmentRepository.hasOverlappingAppointmentExcludingId(
                    barber.getId(),
                    AppointmentStatus.CANCELED,
                    startTime.plusMinutes(30),
                    startTime
            )).thenReturn(true);

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(AppointmentTimeSlotUnavailable.class);

            verify(appointmentRepository, never()).save(any(Appointment.class));
            verify(appointmentProcedureRepository, never())
                    .save(any(AppointmentProcedure.class));
        }

        @Test
        @DisplayName("Should throw exception when customer does not exist")
        void shouldThrowExceptionWhenCustomerDoesNotExist() {

            AppointmentCreateRequest request =
                    createRequest(List.of(1L));

            when(customerRepository.findById(customer.getId()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(CustomerNotFound.class);

            verify(barberRepository, never()).findById(any());
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw exception when barber does not exist")
        void shouldThrowExceptionWhenBarberDoesNotExist() {

            AppointmentCreateRequest request =
                    createRequest(List.of(1L));

            when(customerRepository.findById(customer.getId()))
                    .thenReturn(Optional.of(customer));

            when(barberRepository.findById(barber.getId()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(BarberNotFound.class);

            verify(procedureRepository, never()).findAllById(anyList());
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw exception when start time is null")
        void shouldThrowExceptionWhenStartTimeIsNull() {

            AppointmentCreateRequest request = new AppointmentCreateRequest(
                    customer.getId(),
                    barber.getId(),
                    null,
                    "Test appointment",
                    List.of(1L)
            );

            mockCustomerAndBarber();

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(AppointmentInvalidTimeInterval.class);

            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw exception when procedure list is null")
        void shouldThrowExceptionWhenProcedureListIsNull() {

            AppointmentCreateRequest request = createRequest(null);

            mockCustomerAndBarber();

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(AppointmentProceduresRequired.class);

            verify(procedureRepository, never()).findAllById(anyList());
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw exception when procedure list is empty")
        void shouldThrowExceptionWhenProcedureListIsEmpty() {

            AppointmentCreateRequest request =
                    createRequest(List.of());

            mockCustomerAndBarber();

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(AppointmentProceduresRequired.class);

            verify(procedureRepository, never()).findAllById(anyList());
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw exception when procedures are duplicated")
        void shouldThrowExceptionWhenProceduresAreDuplicated() {

            AppointmentCreateRequest request =
                    createRequest(List.of(1L, 1L));

            mockCustomerAndBarber();

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(AppointmentDuplicateProcedure.class);

            verify(procedureRepository, never()).findAllById(anyList());
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw exception when procedure ID is null")
        void shouldThrowExceptionWhenProcedureIdIsNull() {

            List<Long> procedureIds = new java.util.ArrayList<>();
            procedureIds.add(1L);
            procedureIds.add(null);

            AppointmentCreateRequest request =
                    createRequest(procedureIds);

            mockCustomerAndBarber();

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(AppointmentProcedureNotFound.class);

            verify(procedureRepository, never()).findAllById(anyList());
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw exception when procedure does not exist")
        void shouldThrowExceptionWhenProcedureDoesNotExist() {

            AppointmentCreateRequest request =
                    createRequest(List.of(99L));

            mockCustomerAndBarber();

            when(procedureRepository.findAllById(anyList()))
                    .thenReturn(List.of());

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(AppointmentProcedureNotFound.class);

            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw exception when one of multiple procedures is missing")
        void shouldThrowExceptionWhenOneProcedureIsMissing() {

            Procedure haircut = createProcedure(1L, 30);

            AppointmentCreateRequest request =
                    createRequest(List.of(1L, 99L));

            mockCustomerAndBarber();

            when(procedureRepository.findAllById(anyList()))
                    .thenReturn(List.of(haircut));

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(AppointmentProcedureNotFound.class);

            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw exception when procedure belongs to another barber")
        void shouldThrowExceptionWhenProcedureBelongsToAnotherBarber() {

            Procedure haircut = createProcedure(1L, 30);

            Barber otherBarber = BarberFixtures.withId(2L);
            ReflectionTestUtils.setField(haircut, "barber", otherBarber);

            AppointmentCreateRequest request =
                    createRequest(List.of(1L));

            mockCustomerAndBarber();

            when(procedureRepository.findAllById(anyList()))
                    .thenReturn(List.of(haircut));

            assertThatThrownBy(() -> commandService.addAppointment(request))
                    .isInstanceOf(AppointmentProcedureBarberMismatch.class);

            verify(appointmentRepository, never()).save(any(Appointment.class));
            verify(appointmentProcedureRepository, never())
                    .save(any(AppointmentProcedure.class));
        }
    }

    @Nested
    @DisplayName("Update appointment")
    class UpdateAppointment {

        @Test
        @DisplayName("Should update appointment times successfully")
        void shouldUpdateAppointmentTimesSuccessfully() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);

            LocalDateTime newStartTime = startTime.plusHours(2);
            LocalDateTime newEndTime = endTime.plusHours(2);

            AppointmentUpdateRequest request = new AppointmentUpdateRequest(
                    newStartTime,
                    newEndTime,
                    null
            );

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            when(appointmentRepository.hasOverlappingAppointment(
                    barber.getId(),
                    appointment.getId(),
                    AppointmentStatus.CANCELED,
                    newEndTime,
                    newStartTime
            )).thenReturn(false);

            AppointmentResponse response =
                    commandService.updateAppointment(appointment.getId(), request);

            assertThat(response.startTime()).isEqualTo(newStartTime);
            assertThat(response.endTime()).isEqualTo(newEndTime);
            assertThat(response.notes()).isEqualTo("Test appointment");

            verify(appointmentRepository).hasOverlappingAppointment(
                    barber.getId(),
                    appointment.getId(),
                    AppointmentStatus.CANCELED,
                    newEndTime,
                    newStartTime
            );
        }

        @Test
        @DisplayName("Should update only notes")
        void shouldUpdateOnlyNotes() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);

            AppointmentUpdateRequest request = new AppointmentUpdateRequest(
                    null,
                    null,
                    "Updated notes"
            );

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            AppointmentResponse response =
                    commandService.updateAppointment(appointment.getId(), request);

            assertThat(response.startTime()).isEqualTo(startTime);
            assertThat(response.endTime()).isEqualTo(endTime);
            assertThat(response.notes()).isEqualTo("Updated notes");

            verify(appointmentRepository, never()).hasOverlappingAppointment(
                    any(), any(), any(), any(), any()
            );
        }

        @Test
        @DisplayName("Should update only start time")
        void shouldUpdateOnlyStartTime() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);

            LocalDateTime newStartTime = startTime.plusMinutes(10);

            AppointmentUpdateRequest request = new AppointmentUpdateRequest(
                    newStartTime,
                    null,
                    null
            );

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            AppointmentResponse response =
                    commandService.updateAppointment(appointment.getId(), request);

            assertThat(response.startTime()).isEqualTo(newStartTime);
            assertThat(response.endTime()).isEqualTo(endTime);

            verify(appointmentRepository).hasOverlappingAppointment(
                    barber.getId(),
                    appointment.getId(),
                    AppointmentStatus.CANCELED,
                    endTime,
                    newStartTime
            );
        }

        @Test
        @DisplayName("Should update only end time")
        void shouldUpdateOnlyEndTime() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);

            LocalDateTime newEndTime = endTime.plusMinutes(15);

            AppointmentUpdateRequest request = new AppointmentUpdateRequest(
                    null,
                    newEndTime,
                    null
            );

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            AppointmentResponse response =
                    commandService.updateAppointment(appointment.getId(), request);

            assertThat(response.startTime()).isEqualTo(startTime);
            assertThat(response.endTime()).isEqualTo(newEndTime);

            verify(appointmentRepository).hasOverlappingAppointment(
                    barber.getId(),
                    appointment.getId(),
                    AppointmentStatus.CANCELED,
                    newEndTime,
                    startTime
            );
        }

        @Test
        @DisplayName("Should not check overlap when times are unchanged")
        void shouldNotCheckOverlapWhenTimesAreUnchanged() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);

            AppointmentUpdateRequest request = new AppointmentUpdateRequest(
                    startTime,
                    endTime,
                    "New notes"
            );

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            AppointmentResponse response =
                    commandService.updateAppointment(appointment.getId(), request);

            assertThat(response.notes()).isEqualTo("New notes");

            verify(appointmentRepository, never()).hasOverlappingAppointment(
                    any(), any(), any(), any(), any()
            );
        }

        @Test
        @DisplayName("Should throw exception when appointment does not exist")
        void shouldThrowExceptionWhenAppointmentDoesNotExist() {
            AppointmentUpdateRequest request = new AppointmentUpdateRequest(
                    startTime,
                    endTime,
                    "Updated notes"
            );

            when(appointmentRepository.findById(AppointmentFixtures.KNOWN_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    commandService.updateAppointment(AppointmentFixtures.KNOWN_ID, request)
            ).isInstanceOf(AppointmentNotFound.class);
        }

        @Test
        @DisplayName("Should throw exception when new time slot is unavailable")
        void shouldThrowExceptionWhenNewTimeSlotIsUnavailable() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);

            LocalDateTime newStartTime = startTime.plusHours(2);
            LocalDateTime newEndTime = endTime.plusHours(2);

            AppointmentUpdateRequest request = new AppointmentUpdateRequest(
                    newStartTime,
                    newEndTime,
                    null
            );

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            when(appointmentRepository.hasOverlappingAppointment(
                    barber.getId(),
                    appointment.getId(),
                    AppointmentStatus.CANCELED,
                    newEndTime,
                    newStartTime
            )).thenReturn(true);

            assertThatThrownBy(() ->
                    commandService.updateAppointment(appointment.getId(), request)
            ).isInstanceOf(AppointmentTimeSlotUnavailable.class);

            assertThat(appointment.getStartTime()).isEqualTo(startTime);
            assertThat(appointment.getEndTime()).isEqualTo(endTime);
        }

        @Test
        @DisplayName("Should throw exception when end time is before start time")
        void shouldThrowExceptionWhenEndTimeIsBeforeStartTime() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);

            AppointmentUpdateRequest request = new AppointmentUpdateRequest(
                    startTime,
                    startTime.minusMinutes(10),
                    null
            );

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            assertThatThrownBy(() ->
                    commandService.updateAppointment(appointment.getId(), request)
            ).isInstanceOf(AppointmentInvalidTimeInterval.class);

            verify(appointmentRepository, never()).hasOverlappingAppointment(
                    any(), any(), any(), any(), any()
            );
        }

        @Test
        @DisplayName("Should throw exception when start and end times are equal")
        void shouldThrowExceptionWhenStartAndEndTimesAreEqual() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);

            AppointmentUpdateRequest request = new AppointmentUpdateRequest(
                    startTime,
                    startTime,
                    null
            );

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            assertThatThrownBy(() ->
                    commandService.updateAppointment(appointment.getId(), request)
            ).isInstanceOf(AppointmentInvalidTimeInterval.class);
        }

        @Test
        @DisplayName("Should throw exception when appointment is canceled")
        void shouldThrowExceptionWhenAppointmentIsCanceled() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);
            appointment.setStatus(AppointmentStatus.CANCELED);

            AppointmentUpdateRequest request = new AppointmentUpdateRequest(
                    startTime.plusHours(1),
                    endTime.plusHours(1),
                    null
            );

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            assertThatThrownBy(() ->
                    commandService.updateAppointment(appointment.getId(), request)
            ).isInstanceOf(AppointmentInvalidStatus.class);
        }

        @Test
        @DisplayName("Should throw exception when appointment is completed")
        void shouldThrowExceptionWhenAppointmentIsCompleted() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);
            appointment.setStatus(AppointmentStatus.COMPLETED);

            AppointmentUpdateRequest request = new AppointmentUpdateRequest(
                    startTime.plusHours(1),
                    endTime.plusHours(1),
                    null
            );

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            assertThatThrownBy(() ->
                    commandService.updateAppointment(appointment.getId(), request)
            ).isInstanceOf(AppointmentInvalidStatus.class);
        }
    }

    @Nested
    @DisplayName("Confirm appointment")
    class ConfirmAppointment {

        @Test
        @DisplayName("Should confirm booked appointment successfully")
        void shouldConfirmBookedAppointmentSuccessfully() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            AppointmentResponse response =
                    commandService.confirmAppointment(appointment.getId());

            assertThat(response.status()).isEqualTo(AppointmentStatus.CONFIRMED);
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        }

        @Test
        @DisplayName("Should throw exception when appointment does not exist")
        void shouldThrowExceptionWhenAppointmentDoesNotExist() {
            when(appointmentRepository.findById(AppointmentFixtures.KNOWN_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    commandService.confirmAppointment(AppointmentFixtures.KNOWN_ID)
            ).isInstanceOf(AppointmentNotFound.class);
        }

        @Test
        @DisplayName("Should throw exception when appointment is not booked")
        void shouldThrowExceptionWhenAppointmentIsNotBooked() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);
            appointment.setStatus(AppointmentStatus.CONFIRMED);

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            assertThatThrownBy(() ->
                    commandService.confirmAppointment(appointment.getId())
            ).isInstanceOf(AppointmentInvalidStatus.class);

            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        }
    }

    @Nested
    @DisplayName("Cancel appointment")
    class CancelAppointment {

        @Test
        @DisplayName("Should cancel booked appointment successfully")
        void shouldCancelBookedAppointmentSuccessfully() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            AppointmentResponse response =
                    commandService.cancelAppointment(appointment.getId());

            assertThat(response.status()).isEqualTo(AppointmentStatus.CANCELED);
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELED);
        }

        @Test
        @DisplayName("Should cancel confirmed appointment successfully")
        void shouldCancelConfirmedAppointmentSuccessfully() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);
            appointment.setStatus(AppointmentStatus.CONFIRMED);

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            AppointmentResponse response =
                    commandService.cancelAppointment(appointment.getId());

            assertThat(response.status()).isEqualTo(AppointmentStatus.CANCELED);
        }

        @Test
        @DisplayName("Should throw exception when appointment does not exist")
        void shouldThrowExceptionWhenAppointmentDoesNotExist() {
            when(appointmentRepository.findById(AppointmentFixtures.KNOWN_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    commandService.cancelAppointment(AppointmentFixtures.KNOWN_ID)
            ).isInstanceOf(AppointmentNotFound.class);
        }

        @Test
        @DisplayName("Should throw exception when appointment is already canceled")
        void shouldThrowExceptionWhenAppointmentIsAlreadyCanceled() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);
            appointment.setStatus(AppointmentStatus.CANCELED);

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            assertThatThrownBy(() ->
                    commandService.cancelAppointment(appointment.getId())
            ).isInstanceOf(AppointmentCannotBeCanceled.class);
        }

        @Test
        @DisplayName("Should throw exception when appointment is completed")
        void shouldThrowExceptionWhenAppointmentIsCompleted() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);
            appointment.setStatus(AppointmentStatus.COMPLETED);

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            assertThatThrownBy(() ->
                    commandService.cancelAppointment(appointment.getId())
            ).isInstanceOf(AppointmentCannotBeCanceled.class);
        }
    }

    @Nested
    @DisplayName("Complete appointment")
    class CompleteAppointment {

        @Test
        @DisplayName("Should complete confirmed appointment successfully")
        void shouldCompleteConfirmedAppointmentSuccessfully() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);
            appointment.setStatus(AppointmentStatus.CONFIRMED);

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            AppointmentResponse response =
                    commandService.completeAppointment(appointment.getId());

            assertThat(response.status()).isEqualTo(AppointmentStatus.COMPLETED);
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        }

        @Test
        @DisplayName("Should throw exception when appointment does not exist")
        void shouldThrowExceptionWhenAppointmentDoesNotExist() {
            when(appointmentRepository.findById(AppointmentFixtures.KNOWN_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    commandService.completeAppointment(AppointmentFixtures.KNOWN_ID)
            ).isInstanceOf(AppointmentNotFound.class);
        }

        @Test
        @DisplayName("Should throw exception when appointment is not confirmed")
        void shouldThrowExceptionWhenAppointmentIsNotConfirmed() {
            Appointment appointment = AppointmentFixtures.persisted(customer, barber);

            when(appointmentRepository.findById(appointment.getId()))
                    .thenReturn(Optional.of(appointment));

            assertThatThrownBy(() ->
                    commandService.completeAppointment(appointment.getId())
            ).isInstanceOf(AppointmentInvalidStatus.class);

            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.BOOKED);
        }
    }

}
