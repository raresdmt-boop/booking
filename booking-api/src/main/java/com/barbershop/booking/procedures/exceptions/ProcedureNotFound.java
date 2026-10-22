
package com.barbershop.booking.procedures.exceptions;

public class ProcedureNotFound extends RuntimeException {

    public ProcedureNotFound() {
        super(ProcedureExceptionConstants.PROCEDURE_ID_NOT_FOUND);
    }
}
