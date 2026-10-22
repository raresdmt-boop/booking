
package com.barbershop.booking.procedures.services.interfaces;

import com.barbershop.booking.procedures.dtos.ProcedureResponse;

import java.util.List;

public interface ProcedureQueryService {

    ProcedureResponse getProcedureById(Long id);

    List<ProcedureResponse> getProcedures();

    List<ProcedureResponse> getProceduresByBarberId(Long barberId);
}
