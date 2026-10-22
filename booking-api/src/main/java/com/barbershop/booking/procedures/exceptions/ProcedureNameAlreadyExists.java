
package com.barbershop.booking.procedures.exceptions;

public class ProcedureNameAlreadyExists extends RuntimeException {

    public ProcedureNameAlreadyExists() {
        super(ProcedureExceptionConstants.PROCEDURE_NAME_ALREADY_EXISTS);
    }
}
