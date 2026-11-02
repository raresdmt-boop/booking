
package com.barbershop.booking.appointments.services;

import com.barbershop.booking.appointments.exceptions.AppointmentDuplicateProcedure;
import com.barbershop.booking.appointments.exceptions.AppointmentProcedureBarberMismatch;
import com.barbershop.booking.appointments.exceptions.AppointmentProcedureNotFound;
import com.barbershop.booking.appointments.exceptions.AppointmentProceduresRequired;
import com.barbershop.booking.appointments.models.Appointment;
import com.barbershop.booking.appointments.models.AppointmentStatus;
import com.barbershop.booking.appointments.repositories.AppointmentRepository;
import com.barbershop.booking.appointments.services.appointment.AppointmentAvailabilityServiceImpl;
import com.barbershop.booking.barbers.exceptions.BarberDoesNotHaveThisProcedure;
import com.barbershop.booking.barbers.models.BarberSchedule;
import com.barbershop.booking.barbers.models.BarberTimeOff;
import com.barbershop.booking.barbers.repositories.BarberScheduleRepository;
import com.barbershop.booking.barbers.repositories.BarberTimeOffRepository;
import com.barbershop.booking.procedures.models.Procedure;
import com.barbershop.booking.procedures.repository.ProcedureRepository;
import com.barbershop.booking.support.AppointmentFixtures;
import com.barbershop.booking.support.BarberFixtures;
import com.barbershop.booking.support.CustomerFixtures;
import com.barbershop.booking.support.ProcedureFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentAvailabilityServiceImplTest {

    private static final Long BARBER_ID = 1L;
    private static final LocalDate DATE = LocalDate.of(2026, 10, 26);
    private static final List<Long> PROCEDURE_IDS =
            List.of(ProcedureFixtures.KNOWN_ID);

    @Mock
    private ProcedureRepository procedureRepository;

    @Mock
    private BarberScheduleRepository barberScheduleRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private BarberTimeOffRepository barberTimeOffRepository;

    @InjectMocks
    private AppointmentAvailabilityServiceImpl service;

    private void mockProcedure() {
        when(procedureRepository.findAllById(PROCEDURE_IDS))
                .thenReturn(List.of(ProcedureFixtures.persisted()));
    }

    private void mockSchedule(LocalTime start, LocalTime end) {
        BarberSchedule schedule = new BarberSchedule(
                BarberFixtures.persisted(),
                DATE.getDayOfWeek().getValue(),
                start,
                end
        );

        when(barberScheduleRepository.findByBarberIdAndDayOfWeek(
                BARBER_ID,
                DATE.getDayOfWeek().getValue()
        )).thenReturn(Optional.of(schedule));
    }

    private void mockNoAppointments() {
        when(appointmentRepository.findOverlappingAppointments(
                eq(BARBER_ID),
                eq(AppointmentStatus.CANCELED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of());
    }

    private void mockNoTimeOff() {
        when(barberTimeOffRepository.findOverlappingTimeOff(
                eq(BARBER_ID),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of());
    }

    @Test
    void getAvailableSlots_throwsWhenProcedureIdsAreNull() {
        assertThatThrownBy(() ->
                service.getAvailableSlots(BARBER_ID, DATE, null)
        ).isInstanceOf(AppointmentProceduresRequired.class);

        verifyNoInteractions(procedureRepository);
    }

    @Test
    void getAvailableSlots_throwsWhenProcedureIdsAreEmpty() {
        assertThatThrownBy(() ->
                service.getAvailableSlots(BARBER_ID, DATE, List.of())
        ).isInstanceOf(AppointmentProceduresRequired.class);

        verifyNoInteractions(procedureRepository);
    }

    @Test
    void getAvailableSlots_throwsWhenProcedureIdIsNull() {
        List<Long> ids = new java.util.ArrayList<>();
        ids.add(null);

        assertThatThrownBy(() ->
                service.getAvailableSlots(BARBER_ID, DATE, ids)
        ).isInstanceOf(AppointmentProcedureNotFound.class);

        verifyNoInteractions(procedureRepository);
    }

    @Test
    void getAvailableSlots_throwsWhenProcedureIdsAreDuplicated() {
        List<Long> ids = List.of(
                ProcedureFixtures.KNOWN_ID,
                ProcedureFixtures.KNOWN_ID
        );

        assertThatThrownBy(() ->
                service.getAvailableSlots(BARBER_ID, DATE, ids)
        ).isInstanceOf(AppointmentDuplicateProcedure.class);

        verifyNoInteractions(procedureRepository);
    }

    @Test
    void getAvailableSlots_throwsWhenProcedureDoesNotExist() {
        when(procedureRepository.findAllById(PROCEDURE_IDS))
                .thenReturn(List.of());

        assertThatThrownBy(() ->
                service.getAvailableSlots(BARBER_ID, DATE, PROCEDURE_IDS)
        ).isInstanceOf(AppointmentProcedureNotFound.class);
    }

    @Test
    void getAvailableSlots_throwsWhenProcedureBelongsToAnotherBarber() {
        Long anotherBarberId = 999L;

        mockProcedure();

        assertThatThrownBy(() ->
                service.getAvailableSlots(
                        anotherBarberId,
                        DATE,
                        PROCEDURE_IDS
                )
        ).isInstanceOf(AppointmentProcedureBarberMismatch.class);
    }

    @Test
    void getAvailableSlots_returnsEmptyWhenBarberDoesNotWorkThatDay() {
        mockProcedure();

        when(barberScheduleRepository.findByBarberIdAndDayOfWeek(
                BARBER_ID,
                DATE.getDayOfWeek().getValue()
        )).thenReturn(Optional.empty());

        List<LocalTime> result = service.getAvailableSlots(
                BARBER_ID,
                DATE,
                PROCEDURE_IDS
        );

        assertThat(result).isEmpty();
    }

    @Test
    void getAvailableSlots_returnsEmptyWhenProcedureDoesNotFitSchedule() {
        mockProcedure();
        mockSchedule(LocalTime.of(9, 0), LocalTime.of(9, 15));
        mockNoAppointments();
        mockNoTimeOff();

        List<LocalTime> result = service.getAvailableSlots(
                BARBER_ID,
                DATE,
                PROCEDURE_IDS
        );

        assertThat(result).isEmpty();
    }

    @Test
    void getAvailableSlots_returnsSlotsEvery15Minutes() {
        mockProcedure();
        mockSchedule(LocalTime.of(9, 0), LocalTime.of(10, 0));
        mockNoAppointments();
        mockNoTimeOff();

        List<LocalTime> result = service.getAvailableSlots(
                BARBER_ID,
                DATE,
                PROCEDURE_IDS
        );

        assertThat(result).containsExactly(
                LocalTime.of(9, 0),
                LocalTime.of(9, 15),
                LocalTime.of(9, 30)
        );
    }

    @Test
    void getAvailableSlots_includesSlotEndingExactlyAtClosingTime() {
        mockProcedure();
        mockSchedule(LocalTime.of(9, 0), LocalTime.of(9, 30));
        mockNoAppointments();
        mockNoTimeOff();

        List<LocalTime> result = service.getAvailableSlots(
                BARBER_ID,
                DATE,
                PROCEDURE_IDS
        );

        assertThat(result).containsExactly(LocalTime.of(9, 0));
    }

    @Test
    void getAvailableSlots_excludesOccupiedSlots() {
        mockProcedure();
        mockSchedule(LocalTime.of(9, 0), LocalTime.of(10, 0));

        Appointment appointment = AppointmentFixtures.persisted(
                CustomerFixtures.persisted(),
                BarberFixtures.persisted()
        );

        appointment.setStartTime(DATE.atTime(9, 15));
        appointment.setEndTime(DATE.atTime(9, 45));

        when(appointmentRepository.findOverlappingAppointments(
                eq(BARBER_ID),
                eq(AppointmentStatus.CANCELED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(appointment));

        mockNoTimeOff();

        List<LocalTime> result = service.getAvailableSlots(
                BARBER_ID,
                DATE,
                PROCEDURE_IDS
        );

        assertThat(result).isEmpty();
    }

    @Test
    void getAvailableSlots_excludesSlotsOverlappingTimeOff() {
        mockProcedure();
        mockSchedule(LocalTime.of(9, 0), LocalTime.of(10, 0));
        mockNoAppointments();

        BarberTimeOff timeOff = new BarberTimeOff(
                BarberFixtures.persisted(),
                DATE.atTime(9, 15),
                DATE.atTime(9, 45),
                "Personal time off"
        );

        when(barberTimeOffRepository.findOverlappingTimeOff(
                eq(BARBER_ID),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(timeOff));

        List<LocalTime> result = service.getAvailableSlots(
                BARBER_ID,
                DATE,
                PROCEDURE_IDS
        );

        assertThat(result).isEmpty();
    }

    @Test
    void getAvailableSlots_keepsSlotEndingExactlyWhenTimeOffStarts() {
        mockProcedure();
        mockSchedule(LocalTime.of(9, 0), LocalTime.of(9, 45));
        mockNoAppointments();

        BarberTimeOff timeOff = new BarberTimeOff(
                BarberFixtures.persisted(),
                DATE.atTime(9, 30),
                DATE.atTime(10, 0),
                "Personal time off"
        );

        when(barberTimeOffRepository.findOverlappingTimeOff(
                eq(BARBER_ID),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(timeOff));

        List<LocalTime> result = service.getAvailableSlots(
                BARBER_ID,
                DATE,
                PROCEDURE_IDS
        );

        assertThat(result).containsExactly(LocalTime.of(9, 0));
    }

    @Test
    void getAvailableSlots_returnsEmptyWhenAllSlotsAreOccupied() {
        mockProcedure();
        mockSchedule(LocalTime.of(9, 0), LocalTime.of(10, 0));

        Appointment appointment = AppointmentFixtures.persisted(
                CustomerFixtures.persisted(),
                BarberFixtures.persisted()
        );

        appointment.setStartTime(DATE.atTime(9, 0));
        appointment.setEndTime(DATE.atTime(10, 0));

        when(appointmentRepository.findOverlappingAppointments(
                eq(BARBER_ID),
                eq(AppointmentStatus.CANCELED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(appointment));
        mockNoTimeOff();

        List<LocalTime> result = service.getAvailableSlots(
                BARBER_ID,
                DATE,
                PROCEDURE_IDS
        );

        assertThat(result).isEmpty();
    }

    @Test
    @Timeout(2)
    void getAvailableSlots_doesNotLoopForeverNearMidnight() {
        Procedure procedure = ProcedureFixtures.persisted();
        procedure.setDurationMinutes(10);

        when(procedureRepository.findAllById(PROCEDURE_IDS))
                .thenReturn(List.of(procedure));

        mockSchedule(LocalTime.of(22, 0), LocalTime.of(23, 59));
        mockNoAppointments();
        mockNoTimeOff();

        List<LocalTime> result = service.getAvailableSlots(
                BARBER_ID,
                DATE,
                PROCEDURE_IDS
        );

        assertThat(result)
                .hasSize(8)
                .startsWith(LocalTime.of(22, 0))
                .endsWith(LocalTime.of(23, 45));
    }
}
