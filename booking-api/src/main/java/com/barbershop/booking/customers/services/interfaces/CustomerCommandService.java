package com.barbershop.booking.customers.services.interfaces;

import com.barbershop.booking.customers.dtos.CustomerCreateRequest;
import com.barbershop.booking.customers.dtos.CustomerUpdateRequest;
import com.barbershop.booking.customers.dtos.CustomerResponse;

public interface CustomerCommandService {

    CustomerResponse addCustomer(CustomerCreateRequest request);

    CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request);

    CustomerResponse deleteCustomer(Long id);
}