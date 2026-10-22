
package com.barbershop.booking.barbers.dtos;

import com.barbershop.booking.barbers.models.Barber;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "BarberResponse",
        description = "Barber profile information")
public record BarberResponse(

        Long id,
        Long userId,
        String firstName,
        String lastName,
        String phone,
        boolean active,
        LocalDateTime createdAt

) {

    public static BarberResponse from(Barber barber) {
        return new BarberResponse(
                barber.getId(),
                barber.getUser().getId(),
                barber.getFirstName(),
                barber.getLastName(),
                barber.getPhone(),
                barber.isActive(),
                barber.getCreatedAt()
        );
    }
}
