package com.barbershop.booking.appointments.services.appointment;

import com.barbershop.booking.appointments.exceptions.AppointmentDuplicateProcedure;
import com.barbershop.booking.appointments.exceptions.AppointmentProcedureNotFound;
import com.barbershop.booking.appointments.exceptions.AppointmentProceduresRequired;
import com.barbershop.booking.appointments.repositories.AppointmentRepository;
import com.barbershop.booking.appointments.services.appointment.interfaces.AppointmentAvailabilityService;
import com.barbershop.booking.barbers.exceptions.BarberDoesNotHaveThisProcedure;
import com.barbershop.booking.barbers.models.BarberSchedule;
import com.barbershop.booking.barbers.repositories.BarberScheduleRepository;
import com.barbershop.booking.barbers.repositories.BarberTimeOffRepository;
import com.barbershop.booking.procedures.models.Procedure;
import com.barbershop.booking.procedures.repository.ProcedureRepository;
import com.barbershop.booking.appointments.models.AppointmentStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AppointmentAvailabilityServiceImpl
        implements AppointmentAvailabilityService {

    private final ProcedureRepository procedureRepository;
    private final BarberScheduleRepository barberScheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final BarberTimeOffRepository barberTimeOffRepository;

    public AppointmentAvailabilityServiceImpl(ProcedureRepository procedureRepository, BarberScheduleRepository barberScheduleRepository, AppointmentRepository appointmentRepository, BarberTimeOffRepository barberTimeOffRepository) {
        this.procedureRepository = procedureRepository;
        this.barberScheduleRepository = barberScheduleRepository;
        this.appointmentRepository = appointmentRepository;
        this.barberTimeOffRepository = barberTimeOffRepository;
    }



    @Override
    public List<LocalTime> getAvailableSlots(
            Long barberId,
            LocalDate date,
            List<Long> procedureIds) {

        if (procedureIds == null || procedureIds.isEmpty()) {
            throw new AppointmentProceduresRequired();
        }

        for (Long procedureId : procedureIds) {
            if (procedureId == null) {
                throw new AppointmentProcedureNotFound();
            }
        }

        Set<Long> uniqueIds = new HashSet<>(procedureIds);

        if (uniqueIds.size() != procedureIds.size()) {
            throw new AppointmentDuplicateProcedure();
        }

        List<Procedure> procedures = procedureRepository.findAllById(procedureIds);

        if (procedures.size() != procedureIds.size()) {
            throw new AppointmentProcedureNotFound();
        }

        for (Procedure procedure : procedures) {
            if (!procedure.getBarber().getId().equals(barberId)) {
                throw new BarberDoesNotHaveThisProcedure();
            }
        }

        int totalDuration = 0;

        for (Procedure procedure : procedures) {
            totalDuration += procedure.getDurationMinutes();
        }

        if (totalDuration >= 24 * 60) {
            return List.of();
        }

        int dayOfWeek = date.getDayOfWeek().getValue();

        BarberSchedule schedule = barberScheduleRepository
                .findByBarberIdAndDayOfWeek(barberId, dayOfWeek)
                .orElse(null);

        if (schedule == null) {
            return List.of();
        }

        List<LocalTime> availableSlots = new ArrayList<>();

        LocalDateTime slotStart = date.atTime(schedule.getStartTime());
        LocalDateTime closingDateTime = date.atTime(schedule.getEndTime());

        while (!slotStart.plusMinutes(totalDuration).isAfter(closingDateTime)) {

            LocalDateTime slotEnd = slotStart.plusMinutes(totalDuration);

            boolean occupied = appointmentRepository.hasOverlappingAppointmentExcludingId(
                    barberId,
                    AppointmentStatus.CANCELED,
                    slotEnd,
                    slotStart
            );

            boolean timeOff = barberTimeOffRepository.hasOverlappingTimeOff(
                    barberId,
                    slotStart,
                    slotEnd
            );

            if (!occupied && !timeOff) {
                availableSlots.add(slotStart.toLocalTime());
            }

            slotStart = slotStart.plusMinutes(15);
        }

        return availableSlots;
    }
}