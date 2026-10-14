
package com.barbershop.booking.procedures.services;

import com.barbershop.booking.procedures.models.Procedure;
import java.util.List;

public interface ProcedureQueryService {

    Procedure getProcedureById(Long id);

    List<Procedure> getAllProcedures();

}
