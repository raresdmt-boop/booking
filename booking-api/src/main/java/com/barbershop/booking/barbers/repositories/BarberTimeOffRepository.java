
package com.barbershop.booking.barbers.repositories;

import com.barbershop.booking.barbers.models.BarberTimeOff;
import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface BarberTimeOffRepository extends JpaRepository<BarberTimeOff, Long> {

    @Query("""
        SELECT COUNT(t) > 0
        FROM BarberTimeOff t
        WHERE t.barber.id = :barberId
        AND t.startDatetime < :endTime
        AND t.endDatetime > :startTime
        """)
    boolean hasOverlappingTimeOff(
            @Param("barberId") Long barberId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
