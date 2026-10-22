package com.barbershop.booking.appointments.services.appointmentprocedure.interfaces;

import com.barbershop.booking.appointments.dtos.appointmentprocedure.AppointmentProcedureCreateRequest;
import com.barbershop.booking.appointments.dtos.appointmentprocedure.AppointmentProcedureUpdateRequest;
import com.barbershop.booking.appointments.dtos.appointmentprocedure.AppointmentProcedureResponse;

public interface AppointmentProcedureCommandService {

    AppointmentProcedureResponse addAppointmentProcedure(
            AppointmentProcedureCreateRequest request
    );

    AppointmentProcedureResponse updateAppointmentProcedure(
            Long id,
            AppointmentProcedureUpdateRequest request
    );

    void deleteAppointmentProcedure(Long id);
}
