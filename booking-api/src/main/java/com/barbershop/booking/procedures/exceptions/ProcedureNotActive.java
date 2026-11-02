package com.barbershop.booking.procedures.exceptions;

public class ProcedureNotActive extends RuntimeException {
    public ProcedureNotActive() {
        super(ProcedureExceptionConstants.PROCEDURE_NOT_ACTIVE);
    }
}
