
package com.barbershop.booking.procedures.services.interfaces;

import com.barbershop.booking.procedures.dtos.ProcedureCreateRequest;
import com.barbershop.booking.procedures.dtos.ProcedureResponse;
import com.barbershop.booking.procedures.dtos.ProcedureUpdateRequest;

public interface ProcedureCommandService {

    ProcedureResponse addProcedure(ProcedureCreateRequest request);

    ProcedureResponse updateProcedure(Long id, ProcedureUpdateRequest request);

    ProcedureResponse deleteProcedure(Long id);
}
