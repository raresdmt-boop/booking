package com.barbershop.booking.customers.dtos;

import com.barbershop.booking.customers.models.Customer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "CustomerResponse",
        description = "Customer profile information")
public record CustomerResponse(

        Long id,
        Long userId,
        String firstName,
        String lastName,
        String phone,
        boolean blocked,
        LocalDateTime createdAt

) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getUser().getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhone(),
                customer.isBlocked(),
                customer.getCreatedAt()
        );
    }
}