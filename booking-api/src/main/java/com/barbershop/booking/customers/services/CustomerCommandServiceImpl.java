
package com.barbershop.booking.customers.services;

import com.barbershop.booking.customers.dtos.CustomerCreateRequest;
import com.barbershop.booking.customers.dtos.CustomerResponse;
import com.barbershop.booking.customers.dtos.CustomerUpdateRequest;
import com.barbershop.booking.customers.exceptions.CustomerNotFound;
import com.barbershop.booking.customers.exceptions.CustomerPhoneAlreadyExists;
import com.barbershop.booking.customers.exceptions.CustomerUserAlreadyExists;
import com.barbershop.booking.customers.models.Customer;
import com.barbershop.booking.customers.repository.CustomerRepository;
import com.barbershop.booking.customers.services.interfaces.CustomerCommandService;
import com.barbershop.booking.users.exceptions.UserNotFound;
import com.barbershop.booking.users.models.User;
import com.barbershop.booking.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CustomerCommandServiceImpl implements CustomerCommandService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerCommandServiceImpl(CustomerRepository customerRepository,
                                      UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public CustomerResponse addCustomer(CustomerCreateRequest request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(UserNotFound::new);

        if (customerRepository.existsByUserId(request.userId())) {
            throw new CustomerUserAlreadyExists();
        }

        if (customerRepository.existsByPhone(request.phone())) {
            throw new CustomerPhoneAlreadyExists();
        }

        Customer customer = new Customer(
                user,
                request.firstName(),
                request.lastName(),
                request.phone()
        );

        Customer savedCustomer = customerRepository.save(customer);

        return CustomerResponse.from(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFound::new);

        if (request.firstName() != null && !request.firstName().isBlank()) {
            customer.setFirstName(request.firstName());
        }

        if (request.lastName() != null && !request.lastName().isBlank()) {
            customer.setLastName(request.lastName());
        }

        if (request.phone() != null && !request.phone().isBlank()) {

            if (!request.phone().equals(customer.getPhone())
                    && customerRepository.existsByPhone(request.phone())) {
                throw new CustomerPhoneAlreadyExists();
            }

            customer.setPhone(request.phone());
        }

        if (request.blocked() != null) {
            customer.setBlocked(request.blocked());
        }

        return CustomerResponse.from(customer);
    }

    @Override
    @Transactional
    public CustomerResponse deleteCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFound::new);

        CustomerResponse deletedCustomer = CustomerResponse.from(customer);

        customerRepository.delete(customer);

        return deletedCustomer;
    }
}
