
package com.barbershop.booking.barbers.repositories;

import com.barbershop.booking.barbers.models.BarberSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BarberScheduleRepository extends JpaRepository<BarberSchedule, Long> {

    Optional<BarberSchedule> findByBarberIdAndDayOfWeek(Long barberId, int dayOfWeek);

}
