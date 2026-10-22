
package com.barbershop.booking.barbers.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BarberUpdateRequest",
        description = "Data used to update a barber profile")
public record BarberUpdateRequest(

        @Schema(description = "Barber first name", example = "Andrei")
        String firstName,

        @Schema(description = "Barber last name", example = "Popescu")
        String lastName,

        @Schema(description = "Barber phone number", example = "0712345678")
        String phone,

        @Schema(description = "Whether the barber is active", example = "true")
        Boolean active

) {
}
