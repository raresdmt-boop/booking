
package com.barbershop.booking.appointments.services.appointment;

import com.barbershop.booking.appointments.dtos.appointment.AppointmentResponse;
import com.barbershop.booking.appointments.exceptions.AppointmentNotFound;
import com.barbershop.booking.appointments.repositories.AppointmentRepository;
import com.barbershop.booking.appointments.services.appointment.interfaces.AppointmentQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import com.barbershop.booking.appointments.models.Appointment;
import com.barbershop.booking.appointments.dtos.appointmentprocedure.AppointmentProcedureResponse;
import com.barbershop.booking.appointments.repositories.AppointmentProcedureRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public class AppointmentQueryServiceImpl implements AppointmentQueryService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentProcedureRepository appointmentProcedureRepository;

    public AppointmentQueryServiceImpl(AppointmentRepository appointmentRepository, AppointmentProcedureRepository appointmentProcedureRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentProcedureRepository = appointmentProcedureRepository;
    }

    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(AppointmentNotFound::new);
    }

    @Override
    public List<AppointmentResponse> getAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByCustomerId(Long customerId) {
        return appointmentRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByBarberId(Long barberId) {
        return appointmentRepository.findByBarberId(barberId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByBarberIdAndPeriod(
            Long barberId,
            LocalDateTime start,
            LocalDateTime end) {

        return appointmentRepository.findByBarberIdAndStartTimeBetween(
                        barberId, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AppointmentResponse toResponse(Appointment appointment) {

        List<AppointmentProcedureResponse> procedures =
                appointmentProcedureRepository.findByAppointmentId(appointment.getId())
                        .stream()
                        .map(AppointmentProcedureResponse::from)
                        .toList();

        return AppointmentResponse.from(appointment, procedures);
    }
}
