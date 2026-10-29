
package com.barbershop.booking.appointments.repositories;

import com.barbershop.booking.appointments.models.Appointment;
import com.barbershop.booking.appointments.models.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByCustomerId(Long customerId);

    List<Appointment> findByBarberId(Long barberId);

    List<Appointment> findByBarberIdAndStartTimeBetween(
            Long barberId,
            LocalDateTime start,
            LocalDateTime end
    );


    @Query("""
        SELECT COUNT(a) > 0
        FROM Appointment a
        WHERE a.barber.id = :barberId
        AND a.status <> :status
        AND a.startTime < :endTime
        AND a.endTime > :startTime
        """)
    boolean hasOverlappingAppointmentExcludingId(
            Long barberId,
            AppointmentStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime
    );


    boolean existsByBarberIdAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long barberId,
            AppointmentStatus status,
            LocalDateTime endTime,
            LocalDateTime startTime
    );


    @Query("""
        SELECT COUNT(a) > 0
        FROM Appointment a
        WHERE a.barber.id = :barberId
        AND a.id <> :appointmentId
        AND a.status <> :status
        AND a.startTime < :endTime
        AND a.endTime > :startTime
        """)
    boolean hasOverlappingAppointment(
            Long barberId,
            Long appointmentId,
            AppointmentStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime
    );


    boolean existsByBarberIdAndIdNotAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long barberId,
            Long appointmentId,
            AppointmentStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime
    );


}
