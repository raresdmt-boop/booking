package com.barbershop.booking.appointments.services.appointmentprocedure.interfaces;

import com.barbershop.booking.appointments.dtos.appointmentprocedure.AppointmentProcedureResponse;

import java.util.List;

public interface AppointmentProcedureQueryService {

    AppointmentProcedureResponse getAppointmentProcedureById(Long id);

    List<AppointmentProcedureResponse> getAppointmentProceduresByAppointmentId(
            Long appointmentId
    );
}
