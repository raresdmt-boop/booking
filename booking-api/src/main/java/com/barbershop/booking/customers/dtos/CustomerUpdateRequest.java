package com.barbershop.booking.customers.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(name = "CustomerUpdateRequest",
        description = "Data used to update a customer profile")
public record CustomerUpdateRequest(

        @Schema(description = "Customer first name", example = "Ion")
        String firstName,

        @Schema(description = "Customer last name", example = "Popescu")
        String lastName,

        @Schema(description = "Customer phone number", example = "0712345678")
        String phone,

        @Schema(description = "Whether the customer is blocked", example = "false")
        Boolean blocked

) {
}