
package com.barbershop.booking.procedures.dtos;

import com.barbershop.booking.procedures.models.Procedure;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "ProcedureResponse",
        description = "Procedure information")
public record ProcedureResponse(

        Long id,
        Long barberId,
        String name,
        String description,
        Integer durationMinutes,
        BigDecimal price,
        boolean active

) {

    public static ProcedureResponse from(Procedure procedure) {
        return new ProcedureResponse(
                procedure.getId(),
                procedure.getBarber().getId(),
                procedure.getName(),
                procedure.getDescription(),
                procedure.getDurationMinutes(),
                procedure.getPrice(),
                procedure.isActive()
        );
    }
}
