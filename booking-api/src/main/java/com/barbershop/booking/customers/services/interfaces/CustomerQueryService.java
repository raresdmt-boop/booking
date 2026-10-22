package com.barbershop.booking.customers.services.interfaces;

import com.barbershop.booking.customers.dtos.CustomerResponse;

import java.util.List;

public interface CustomerQueryService {

    CustomerResponse getCustomerById(Long id);

    List<CustomerResponse> getCustomers();
}