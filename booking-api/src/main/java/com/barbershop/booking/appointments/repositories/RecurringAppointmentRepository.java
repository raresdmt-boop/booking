
package com.barbershop.booking.appointments.repositories;

import com.barbershop.booking.appointments.models.RecurringAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringAppointmentRepository extends JpaRepository<RecurringAppointment, Long> {

}
