
package com.barbershop.booking.appointments.services.appointment;

import com.barbershop.booking.appointments.dtos.appointment.AppointmentCreateRequest;
import com.barbershop.booking.appointments.dtos.appointment.AppointmentResponse;
import com.barbershop.booking.appointments.dtos.appointment.AppointmentUpdateRequest;
import com.barbershop.booking.appointments.dtos.appointmentprocedure.AppointmentProcedureResponse;
import com.barbershop.booking.appointments.exceptions.*;
import com.barbershop.booking.appointments.models.Appointment;
import com.barbershop.booking.appointments.models.AppointmentStatus;
import com.barbershop.booking.appointments.repositories.AppointmentRepository;
import com.barbershop.booking.appointments.services.appointment.interfaces.AppointmentCommandService;
import com.barbershop.booking.barbers.exceptions.BarberNotFound;
import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.barbers.models.BarberSchedule;
import com.barbershop.booking.barbers.repositories.BarberRepository;
import com.barbershop.booking.barbers.repositories.BarberScheduleRepository;
import com.barbershop.booking.barbers.repositories.BarberTimeOffRepository;
import com.barbershop.booking.customers.exceptions.CustomerNotFound;
import com.barbershop.booking.customers.models.Customer;
import com.barbershop.booking.customers.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import com.barbershop.booking.appointments.models.AppointmentProcedure;
import com.barbershop.booking.appointments.repositories.AppointmentProcedureRepository;
import com.barbershop.booking.procedures.models.Procedure;
import com.barbershop.booking.procedures.repository.ProcedureRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.time.LocalDateTime;

@Service
@Validated
public class AppointmentCommandServiceImpl implements AppointmentCommandService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final BarberRepository barberRepository;
    private final ProcedureRepository procedureRepository;
    private final AppointmentProcedureRepository appointmentProcedureRepository;
    private final BarberScheduleRepository barberScheduleRepository;
    private final BarberTimeOffRepository barberTimeOffRepository;

    public AppointmentCommandServiceImpl(
            AppointmentRepository appointmentRepository,
            CustomerRepository customerRepository,
            BarberRepository barberRepository, ProcedureRepository procedureRepository, AppointmentProcedureRepository appointmentProcedureRepository, BarberScheduleRepository barberScheduleRepository, BarberTimeOffRepository barberTimeOffRepository) {
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
        this.barberRepository = barberRepository;
        this.procedureRepository = procedureRepository;
        this.appointmentProcedureRepository = appointmentProcedureRepository;
        this.barberScheduleRepository = barberScheduleRepository;
        this.barberTimeOffRepository = barberTimeOffRepository;
    }



    @Override
    @Transactional
    public AppointmentResponse addAppointment(AppointmentCreateRequest request) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(CustomerNotFound::new);

        Barber barber = barberRepository.findById(request.barberId())
                .orElseThrow(BarberNotFound::new);

        if (request.startTime() == null) {
            throw new AppointmentInvalidTimeInterval();
        }

        if (request.procedureIds() == null || request.procedureIds().isEmpty()) {
            throw new AppointmentProceduresRequired();
        }

        for (Long procedureId : request.procedureIds()) {
            if (procedureId == null) {
                throw new AppointmentProcedureNotFound();
            }
        }

        Set<Long> uniqueIds = new HashSet<>(request.procedureIds());

        if (uniqueIds.size() != request.procedureIds().size()) {
            throw new AppointmentDuplicateProcedure();
        }

        List<Procedure> procedures = procedureRepository.findAllById(
                request.procedureIds()
        );

        if (procedures.size() != request.procedureIds().size()) {
            throw new AppointmentProcedureNotFound();
        }

        int totalDuration = 0;

        for (Procedure procedure : procedures) {

            if (!procedure.getBarber().getId().equals(barber.getId())) {
                throw new AppointmentProcedureBarberMismatch();
            }

            totalDuration += procedure.getDurationMinutes();
        }

        LocalDateTime endTime = request.startTime().plusMinutes(totalDuration);

        validateTimeInterval(request.startTime(), endTime);
        validateBarberSchedule(barber.getId(), request.startTime(), endTime);
        validateBarberTimeOff(barber.getId(), request.startTime(), endTime);

        boolean unavailable =
                appointmentRepository.hasOverlappingAppointmentExcludingId(
                        barber.getId(),
                        AppointmentStatus.CANCELED,
                        endTime,
                        request.startTime()
                );

        if (unavailable) {
            throw new AppointmentTimeSlotUnavailable();
        }

        Appointment appointment = new Appointment(
                customer,
                barber,
                request.startTime(),
                endTime,
                request.notes()
        );

        Appointment savedAppointment = appointmentRepository.save(appointment);

        List<AppointmentProcedureResponse> procedureResponses = new ArrayList<>();

        for (Procedure procedure : procedures) {

            AppointmentProcedure appointmentProcedure = new AppointmentProcedure(
                    savedAppointment,
                    procedure,
                    procedure.getPrice(),
                    procedure.getDurationMinutes()
            );

            AppointmentProcedure savedProcedure =
                    appointmentProcedureRepository.save(appointmentProcedure);

            procedureResponses.add(
                    AppointmentProcedureResponse.from(savedProcedure)
            );
        }

        return AppointmentResponse.from(savedAppointment, procedureResponses);
    }




    @Override
    @Transactional
    public AppointmentResponse updateAppointment(Long id, AppointmentUpdateRequest request) {

        Appointment appointment = findAppointment(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new AppointmentInvalidStatus();
        }


        LocalDateTime startTime;

        if (request.startTime() != null) {
            startTime = request.startTime();
        } else {
            startTime = appointment.getStartTime();
        }

        List<AppointmentProcedure> appointmentProcedures =
                appointmentProcedureRepository.findByAppointmentId(appointment.getId());

        int totalDuration = 0;

        for (AppointmentProcedure appointmentProcedure : appointmentProcedures) {
            totalDuration += appointmentProcedure.getDurationMinutes();
        }

        LocalDateTime endTime = startTime.plusMinutes(totalDuration);

        validateTimeInterval(startTime, endTime);



        boolean timeChanged =
                !startTime.equals(appointment.getStartTime())
                        || !endTime.equals(appointment.getEndTime());

        if (timeChanged) {
            validateBarberSchedule(appointment.getBarber().getId(), startTime, endTime);
            validateBarberTimeOff(appointment.getBarber().getId(), startTime, endTime);

            boolean unavailable =
                    appointmentRepository.hasOverlappingAppointment(
                            appointment.getBarber().getId(),
                            appointment.getId(),
                            AppointmentStatus.CANCELED,
                            startTime,
                            endTime
                    );

            if (unavailable) {
                throw new AppointmentTimeSlotUnavailable();
            }
        }

        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);

        if (request.notes() != null) {
            appointment.setNotes(request.notes());
        }

        List<AppointmentProcedureResponse> procedureResponses = new ArrayList<>();

        for (AppointmentProcedure appointmentProcedure : appointmentProcedures) {
            procedureResponses.add(
                    AppointmentProcedureResponse.from(appointmentProcedure)
            );
        }

        return AppointmentResponse.from(appointment, procedureResponses);
    }

    @Override
    @Transactional
    public AppointmentResponse confirmAppointment(Long id) {

        Appointment appointment = findAppointment(id);

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new AppointmentInvalidStatus();
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);

        return AppointmentResponse.from(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponse cancelAppointment(Long id) {

        Appointment appointment = findAppointment(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new AppointmentCannotBeCanceled();
        }

        appointment.setStatus(AppointmentStatus.CANCELED);

        return AppointmentResponse.from(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponse completeAppointment(Long id) {

        Appointment appointment = findAppointment(id);

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new AppointmentInvalidStatus();
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);

        return AppointmentResponse.from(appointment);
    }



    private Appointment findAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(AppointmentNotFound::new);
    }

    private void validateTimeInterval(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null || !endTime.isAfter(startTime)) {
            throw new AppointmentInvalidTimeInterval();
        }
    }

    private void validateBarberSchedule(
            Long barberId,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        int dayOfWeek = startTime.getDayOfWeek().getValue();

        BarberSchedule schedule = barberScheduleRepository
                .findByBarberIdAndDayOfWeek(barberId, dayOfWeek)
                .orElseThrow(AppointmentTimeSlotUnavailable::new);

        if (!startTime.toLocalDate().equals(endTime.toLocalDate())
                || startTime.toLocalTime().isBefore(schedule.getStartTime())
                || endTime.toLocalTime().isAfter(schedule.getEndTime())) {

            throw new AppointmentTimeSlotUnavailable();
        }
    }

    private void validateBarberTimeOff(
            Long barberId,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        boolean unavailable = barberTimeOffRepository.hasOverlappingTimeOff(
                barberId,
                startTime,
                endTime
        );

        if (unavailable) {
            throw new AppointmentTimeSlotUnavailable();
        }
    }
}
