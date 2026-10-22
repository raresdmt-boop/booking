
package com.barbershop.booking.appointments.services;

import com.barbershop.booking.appointments.exceptions.AppointmentDuplicateProcedure;
import com.barbershop.booking.appointments.exceptions.AppointmentProcedureNotFound;
import com.barbershop.booking.appointments.exceptions.AppointmentProceduresRequired;
import com.barbershop.booking.appointments.models.AppointmentStatus;
import com.barbershop.booking.appointments.repositories.AppointmentRepository;
import com.barbershop.booking.appointments.services.appointment.AppointmentAvailabilityServiceImpl;
import com.barbershop.booking.barbers.models.BarberSchedule;
import com.barbershop.booking.barbers.repositories.BarberScheduleRepository;
import com.barbershop.booking.barbers.repositories.BarberTimeOffRepository;
import com.barbershop.booking.procedures.models.Procedure;
import com.barbershop.booking.procedures.repository.ProcedureRepository;
import com.barbershop.booking.support.BarberFixtures;
import com.barbershop.booking.support.ProcedureFixtures;
import org.junit.jupiter.api.Test;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
        when(appointmentRepository.hasOverlappingAppointmentExcludingId(
                eq(BARBER_ID),
                eq(AppointmentStatus.CANCELED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(false);
    }

    private void mockNoTimeOff() {
        when(barberTimeOffRepository.hasOverlappingTimeOff(
                eq(BARBER_ID),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(false);
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
        ).isInstanceOf(AppointmentProcedureNotFound.class);
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

        List<LocalTime> result = service.getAvailableSlots(
                BARBER_ID,
                DATE,
                PROCEDURE_IDS
        );

        assertThat(result).isEmpty();

        verifyNoInteractions(appointmentRepository, barberTimeOffRepository);
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

        when(appointmentRepository.hasOverlappingAppointmentExcludingId(
                eq(BARBER_ID),
                eq(AppointmentStatus.CANCELED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenAnswer(invocation -> {
            LocalDateTime slotStart = invocation.getArgument(3);
            LocalDateTime slotEnd = invocation.getArgument(2);

            LocalDateTime appointmentStart = DATE.atTime(9, 15);
            LocalDateTime appointmentEnd = DATE.atTime(9, 45);

            return slotStart.isBefore(appointmentEnd)
                    && slotEnd.isAfter(appointmentStart);
        });

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

        when(barberTimeOffRepository.hasOverlappingTimeOff(
                eq(BARBER_ID),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenAnswer(invocation -> {
            LocalDateTime slotStart = invocation.getArgument(1);
            LocalDateTime slotEnd = invocation.getArgument(2);

            LocalDateTime timeOffStart = DATE.atTime(9, 15);
            LocalDateTime timeOffEnd = DATE.atTime(9, 45);

            return slotStart.isBefore(timeOffEnd)
                    && slotEnd.isAfter(timeOffStart);
        });

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

        when(barberTimeOffRepository.hasOverlappingTimeOff(
                eq(BARBER_ID),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenAnswer(invocation -> {
            LocalDateTime slotStart = invocation.getArgument(1);
            LocalDateTime slotEnd = invocation.getArgument(2);

            LocalDateTime timeOffStart = DATE.atTime(9, 30);
            LocalDateTime timeOffEnd = DATE.atTime(10, 0);

            return slotStart.isBefore(timeOffEnd)
                    && slotEnd.isAfter(timeOffStart);
        });

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

        when(appointmentRepository.hasOverlappingAppointmentExcludingId(
                eq(BARBER_ID),
                eq(AppointmentStatus.CANCELED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(true);

        mockNoTimeOff();

        List<LocalTime> result = service.getAvailableSlots(
                BARBER_ID,
                DATE,
                PROCEDURE_IDS
        );

        assertThat(result).isEmpty();
    }
}
