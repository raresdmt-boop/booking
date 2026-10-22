
package com.barbershop.booking.barbers.repositories;

import com.barbershop.booking.barbers.models.Barber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarberRepository extends JpaRepository<Barber, Long> {

    boolean existsByUserId(Long userId);

}
