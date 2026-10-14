
package com.barbershop.booking.procedures.services;

import com.barbershop.booking.procedures.models.Procedure;

public interface ProcedureCommandService {

    Procedure createProcedure(Procedure procedure);

    Procedure updateProcedure(Long id, Procedure procedure);

    void deleteProcedure(Long id);

}
