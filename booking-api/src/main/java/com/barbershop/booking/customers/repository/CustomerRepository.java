
package com.barbershop.booking.customers.repository;

import com.barbershop.booking.customers.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByPhone(String phone);

    boolean existsByUserId(Long userId);
}