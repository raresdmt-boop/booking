
package com.barbershop.booking.appointments.repositories;

import com.barbershop.booking.appointments.models.AppointmentProcedure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentProcedureRepository extends JpaRepository<AppointmentProcedure, Long> {

}

