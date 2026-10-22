
package com.barbershop.booking.customers.services;

import com.barbershop.booking.customers.dtos.CustomerCreateRequest;
import com.barbershop.booking.customers.dtos.CustomerResponse;
import com.barbershop.booking.customers.dtos.CustomerUpdateRequest;
import com.barbershop.booking.customers.exceptions.CustomerNotFound;
import com.barbershop.booking.customers.exceptions.CustomerPhoneAlreadyExists;
import com.barbershop.booking.customers.exceptions.CustomerUserAlreadyExists;
import com.barbershop.booking.customers.models.Customer;
import com.barbershop.booking.customers.repository.CustomerRepository;
import com.barbershop.booking.support.CustomerFixtures;
import com.barbershop.booking.support.UserFixtures;
import com.barbershop.booking.users.exceptions.UserNotFound;
import com.barbershop.booking.users.models.User;
import com.barbershop.booking.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerCommandServiceImpl unit tests")
class CustomerCommandServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomerCommandServiceImpl service;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    @Nested
    @DisplayName("addCustomer")
    class AddCustomer {

        @Test
        @DisplayName("Creates customer successfully")
        void addCustomer_validRequest_returnsResponse() {
            User user = UserFixtures.persisted();

            CustomerCreateRequest request = new CustomerCreateRequest(
                    UserFixtures.KNOWN_ID,
                    "Ion",
                    "Popescu",
                    "0712345678"
            );

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(user));

            when(customerRepository.save(any(Customer.class)))
                    .thenAnswer(invocation -> {
                        Customer customer = invocation.getArgument(0);
                        org.springframework.test.util.ReflectionTestUtils
                                .setField(customer, "id", CustomerFixtures.KNOWN_ID);
                        return customer;
                    });

            CustomerResponse response = service.addCustomer(request);

            verify(customerRepository).save(customerCaptor.capture());

            Customer savedCustomer = customerCaptor.getValue();

            assertThat(savedCustomer.getFirstName()).isEqualTo("Ion");
            assertThat(savedCustomer.getLastName()).isEqualTo("Popescu");
            assertThat(savedCustomer.getPhone()).isEqualTo("0712345678");
            assertThat(savedCustomer.getUser()).isSameAs(user);

            assertThat(response.id()).isEqualTo(CustomerFixtures.KNOWN_ID);
            assertThat(response.userId()).isEqualTo(UserFixtures.KNOWN_ID);
            assertThat(response.blocked()).isFalse();

            verify(customerRepository).existsByUserId(UserFixtures.KNOWN_ID);
            verify(customerRepository).existsByPhone("0712345678");
        }

        @Test
        @DisplayName("Throws UserNotFound when user does not exist")
        void addCustomer_unknownUser_throwsException() {
            CustomerCreateRequest request = new CustomerCreateRequest(
                    UserFixtures.OTHER_ID,
                    "Ion",
                    "Popescu",
                    "0712345678"
            );

            when(userRepository.findById(UserFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.addCustomer(request))
                    .isInstanceOf(UserNotFound.class);

            verify(customerRepository, never()).save(any(Customer.class));
            verifyNoInteractions(customerRepository);
        }

        @Test
        @DisplayName("Throws CustomerUserAlreadyExists when user has a customer profile")
        void addCustomer_existingProfile_throwsException() {
            CustomerCreateRequest request = new CustomerCreateRequest(
                    UserFixtures.KNOWN_ID,
                    "Ion",
                    "Popescu",
                    "0712345678"
            );

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(UserFixtures.persisted()));

            when(customerRepository.existsByUserId(UserFixtures.KNOWN_ID))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.addCustomer(request))
                    .isInstanceOf(CustomerUserAlreadyExists.class);

            verify(customerRepository, never()).save(any(Customer.class));
            verify(customerRepository, never()).existsByPhone(any());
        }

        @Test
        @DisplayName("Throws CustomerPhoneAlreadyExists when phone is taken")
        void addCustomer_duplicatePhone_throwsException() {
            CustomerCreateRequest request = new CustomerCreateRequest(
                    UserFixtures.KNOWN_ID,
                    "Ion",
                    "Popescu",
                    "0712345678"
            );

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(UserFixtures.persisted()));

            when(customerRepository.existsByPhone("0712345678"))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.addCustomer(request))
                    .isInstanceOf(CustomerPhoneAlreadyExists.class);

            verify(customerRepository, never()).save(any(Customer.class));
        }
    }

    @Nested
    @DisplayName("updateCustomer")
    class UpdateCustomer {

        @Test
        @DisplayName("Updates customer fields successfully")
        void updateCustomer_validRequest_updatesFields() {
            Customer customer = CustomerFixtures.persisted();

            CustomerUpdateRequest request = new CustomerUpdateRequest(
                    "Maria",
                    "Ionescu",
                    "0798765432",
                    null
            );

            when(customerRepository.findById(CustomerFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(customer));

            CustomerResponse response = service.updateCustomer(
                    CustomerFixtures.KNOWN_ID, request
            );

            assertThat(response.firstName()).isEqualTo("Maria");
            assertThat(response.lastName()).isEqualTo("Ionescu");
            assertThat(response.phone()).isEqualTo("0798765432");

            verify(customerRepository).existsByPhone("0798765432");
            verify(customerRepository, never()).save(any(Customer.class));
        }

        @Test
        @DisplayName("Keeps existing values when request fields are null")
        void updateCustomer_nullFields_keepsExistingValues() {
            Customer customer = CustomerFixtures.persisted();

            CustomerUpdateRequest request = new CustomerUpdateRequest(
                    null, null, null, null
            );

            when(customerRepository.findById(CustomerFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(customer));

            CustomerResponse response = service.updateCustomer(
                    CustomerFixtures.KNOWN_ID, request
            );

            assertThat(response.firstName()).isEqualTo("Ion");
            assertThat(response.lastName()).isEqualTo("Popescu");
            assertThat(response.phone()).isEqualTo("0712345678");
            assertThat(response.blocked()).isFalse();

            verify(customerRepository, never()).existsByPhone(any());
            verify(customerRepository, never()).save(any(Customer.class));
        }

        @Test
        @DisplayName("Ignores blank text fields")
        void updateCustomer_blankFields_keepsExistingValues() {
            Customer customer = CustomerFixtures.persisted();

            CustomerUpdateRequest request = new CustomerUpdateRequest(
                    " ", "", " ", null
            );

            when(customerRepository.findById(CustomerFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(customer));

            CustomerResponse response = service.updateCustomer(
                    CustomerFixtures.KNOWN_ID, request
            );

            assertThat(response.firstName()).isEqualTo("Ion");
            assertThat(response.lastName()).isEqualTo("Popescu");
            assertThat(response.phone()).isEqualTo("0712345678");

            verify(customerRepository, never()).existsByPhone(any());
        }

        @Test
        @DisplayName("Blocks customer")
        void updateCustomer_blockedTrue_blocksCustomer() {
            Customer customer = CustomerFixtures.persisted();

            CustomerUpdateRequest request = new CustomerUpdateRequest(
                    null, null, null, true
            );

            when(customerRepository.findById(CustomerFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(customer));

            CustomerResponse response = service.updateCustomer(
                    CustomerFixtures.KNOWN_ID, request
            );

            assertThat(response.blocked()).isTrue();
            assertThat(customer.isBlocked()).isTrue();
        }

        @Test
        @DisplayName("Unblocks customer")
        void updateCustomer_blockedFalse_unblocksCustomer() {
            Customer customer = CustomerFixtures.persisted();
            customer.setBlocked(true);

            CustomerUpdateRequest request = new CustomerUpdateRequest(
                    null, null, null, false
            );

            when(customerRepository.findById(CustomerFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(customer));

            CustomerResponse response = service.updateCustomer(
                    CustomerFixtures.KNOWN_ID, request
            );

            assertThat(response.blocked()).isFalse();
            assertThat(customer.isBlocked()).isFalse();
        }

        @Test
        @DisplayName("Does not check uniqueness when phone is unchanged")
        void updateCustomer_samePhone_doesNotCheckDuplicate() {
            Customer customer = CustomerFixtures.persisted();

            CustomerUpdateRequest request = new CustomerUpdateRequest(
                    null, null, "0712345678", null
            );

            when(customerRepository.findById(CustomerFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(customer));

            CustomerResponse response = service.updateCustomer(
                    CustomerFixtures.KNOWN_ID, request
            );

            assertThat(response.phone()).isEqualTo("0712345678");

            verify(customerRepository, never()).existsByPhone(any());
        }

        @Test
        @DisplayName("Throws CustomerPhoneAlreadyExists for duplicate phone")
        void updateCustomer_duplicatePhone_throwsException() {
            Customer customer = CustomerFixtures.persisted();

            CustomerUpdateRequest request = new CustomerUpdateRequest(
                    null, null, "0798765432", null
            );

            when(customerRepository.findById(CustomerFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(customer));

            when(customerRepository.existsByPhone("0798765432"))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.updateCustomer(
                    CustomerFixtures.KNOWN_ID, request
            )).isInstanceOf(CustomerPhoneAlreadyExists.class);

            assertThat(customer.getPhone()).isEqualTo("0712345678");

            verify(customerRepository, never()).save(any(Customer.class));
        }

        @Test
        @DisplayName("Throws CustomerNotFound when ID does not exist")
        void updateCustomer_unknownId_throwsException() {
            CustomerUpdateRequest request = new CustomerUpdateRequest(
                    "Maria", null, null, null
            );

            when(customerRepository.findById(CustomerFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateCustomer(
                    CustomerFixtures.OTHER_ID, request
            )).isInstanceOf(CustomerNotFound.class);

            verify(customerRepository, never()).save(any(Customer.class));
        }
    }

    @Nested
    @DisplayName("deleteCustomer")
    class DeleteCustomer {

        @Test
        @DisplayName("Deletes existing customer and returns response")
        void deleteCustomer_existingId_returnsDeletedCustomer() {
            Customer customer = CustomerFixtures.persisted();

            when(customerRepository.findById(CustomerFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(customer));

            CustomerResponse response = service.deleteCustomer(
                    CustomerFixtures.KNOWN_ID
            );

            assertThat(response.id()).isEqualTo(CustomerFixtures.KNOWN_ID);
            assertThat(response.firstName()).isEqualTo("Ion");
            assertThat(response.phone()).isEqualTo("0712345678");

            verify(customerRepository).delete(customer);
        }

        @Test
        @DisplayName("Throws CustomerNotFound when ID does not exist")
        void deleteCustomer_unknownId_throwsException() {
            when(customerRepository.findById(CustomerFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteCustomer(
                    CustomerFixtures.OTHER_ID
            )).isInstanceOf(CustomerNotFound.class);

            verify(customerRepository, never()).delete(any(Customer.class));
        }
    }
}
