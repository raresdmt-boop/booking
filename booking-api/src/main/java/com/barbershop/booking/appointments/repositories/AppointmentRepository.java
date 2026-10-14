
package com.barbershop.booking.appointments.repositories;

import com.barbershop.booking.appointments.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

}

