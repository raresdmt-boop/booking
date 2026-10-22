
package com.barbershop.booking.appointments.repositories;

import com.barbershop.booking.appointments.models.AppointmentProcedure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentProcedureRepository
        extends JpaRepository<AppointmentProcedure, Long> {

    List<AppointmentProcedure> findByAppointmentId(Long appointmentId);

    boolean existsByAppointmentIdAndProcedureId(
            Long appointmentId,
            Long procedureId
    );
}
