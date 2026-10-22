
package com.barbershop.booking.procedures.repository;

import com.barbershop.booking.procedures.models.Procedure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcedureRepository extends JpaRepository<Procedure, Long> {

    boolean existsByBarberIdAndName(Long barberId, String name);

    List<Procedure> findByBarberId(Long barberId);
}
