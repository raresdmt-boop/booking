
package com.barbershop.booking.customers.services;

import com.barbershop.booking.customers.dtos.CustomerResponse;
import com.barbershop.booking.customers.exceptions.CustomerNotFound;
import com.barbershop.booking.customers.models.Customer;
import com.barbershop.booking.customers.repository.CustomerRepository;
import com.barbershop.booking.support.CustomerFixtures;
import com.barbershop.booking.support.UserFixtures;
import com.barbershop.booking.users.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerQueryServiceImpl unit tests")
class CustomerQueryServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerQueryServiceImpl service;

    @Nested
    @DisplayName("getCustomerById")
    class GetCustomerById {

        @Test
        @DisplayName("Returns customer when ID exists")
        void getCustomerById_existingId_returnsResponse() {
            Customer customer = CustomerFixtures.persisted();

            when(customerRepository.findById(CustomerFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(customer));

            CustomerResponse response = service.getCustomerById(
                    CustomerFixtures.KNOWN_ID
            );

            assertThat(response.id()).isEqualTo(CustomerFixtures.KNOWN_ID);
            assertThat(response.firstName()).isEqualTo("Ion");
            assertThat(response.lastName()).isEqualTo("Popescu");
            assertThat(response.phone()).isEqualTo("0712345678");
            assertThat(response.blocked()).isFalse();
            assertThat(response.createdAt()).isEqualTo(customer.getCreatedAt());

            verify(customerRepository).findById(CustomerFixtures.KNOWN_ID);
        }

        @Test
        @DisplayName("Throws CustomerNotFound when ID does not exist")
        void getCustomerById_unknownId_throwsException() {
            when(customerRepository.findById(CustomerFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getCustomerById(
                    CustomerFixtures.OTHER_ID
            )).isInstanceOf(CustomerNotFound.class);

            verify(customerRepository).findById(CustomerFixtures.OTHER_ID);
        }
    }

    @Nested
    @DisplayName("getCustomers")
    class GetCustomers {

        @Test
        @DisplayName("Returns all customers mapped to responses")
        void getCustomers_existingCustomers_returnsResponses() {
            Customer firstCustomer = CustomerFixtures.persisted();

            Customer secondCustomer = CustomerFixtures.withId(
                    CustomerFixtures.OTHER_ID
            );
            secondCustomer.setFirstName("Maria");
            secondCustomer.setLastName("Ionescu");
            secondCustomer.setPhone("0798765432");

            when(customerRepository.findAll())
                    .thenReturn(List.of(firstCustomer, secondCustomer));

            List<CustomerResponse> responses = service.getCustomers();

            assertThat(responses).hasSize(2);

            assertThat(responses)
                    .extracting(CustomerResponse::firstName)
                    .containsExactly("Ion", "Maria");

            assertThat(responses)
                    .extracting(CustomerResponse::phone)
                    .containsExactly("0712345678", "0798765432");

            verify(customerRepository).findAll();
        }

        @Test
        @DisplayName("Returns empty list when no customers exist")
        void getCustomers_noCustomers_returnsEmptyList() {
            when(customerRepository.findAll()).thenReturn(List.of());

            List<CustomerResponse> responses = service.getCustomers();

            assertThat(responses).isEmpty();

            verify(customerRepository).findAll();
        }

        @Test
        @DisplayName("Returns blocked status correctly")
        void getCustomers_blockedCustomer_returnsBlockedTrue() {
            Customer customer = CustomerFixtures.persisted();
            customer.setBlocked(true);

            when(customerRepository.findAll())
                    .thenReturn(List.of(customer));

            List<CustomerResponse> responses = service.getCustomers();

            assertThat(responses).hasSize(1);
            assertThat(responses.getFirst().blocked()).isTrue();
        }
    }
}
