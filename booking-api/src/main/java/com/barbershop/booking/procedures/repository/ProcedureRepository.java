package com.barbershop.booking.procedures.repository;

import com.barbershop.booking.procedures.models.Procedure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcedureRepository extends JpaRepository<Procedure, Long> {

}

