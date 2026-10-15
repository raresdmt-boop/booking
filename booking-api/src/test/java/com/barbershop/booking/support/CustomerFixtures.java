package com.barbershop.booking.support;

import com.barbershop.booking.customers.models.Customer;
import com.barbershop.booking.users.models.User;
import org.springframework.test.util.ReflectionTestUtils;

public final class CustomerFixtures {

    public static final Long KNOWN_ID = 1L;
    public static final Long OTHER_ID = 2L;

    private CustomerFixtures() {
    }

    public static Customer customer() {
        return new Customer(
                UserFixtures.user(),
                "Ion",
                "Popescu",
                "0712345678"
        );
    }

    public static Customer withId(Long id) {
        Customer customer = customer();
        ReflectionTestUtils.setField(customer, "id", id);
        return customer;
    }

    public static Customer persisted() {
        return withId(KNOWN_ID);
    }
}