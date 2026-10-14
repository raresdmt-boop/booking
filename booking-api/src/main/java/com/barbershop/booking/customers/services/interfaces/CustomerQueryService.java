
package com.barbershop.booking.customers.services.interfaces;

import com.barbershop.booking.customers.models.Customer;
import java.util.List;

public interface CustomerQueryService {

    Customer getCustomerById(Long id);

    List<Customer> getAllCustomers();

}
