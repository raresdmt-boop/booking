
package com.barbershop.booking.customers.repository;

import com.barbershop.booking.customers.models.Customer;
import com.barbershop.booking.users.models.Role;
import com.barbershop.booking.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@DisplayName("CustomerRepository integration tests")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer customer;

    @BeforeEach
    void setUp() {
        User user = new User(
                "customer@test.com",
                "password123",
                Role.CUSTOMER
        );

        entityManager.persist(user);

        customer = new Customer(
                user,
                "Ion",
                "Popescu",
                "0712345678"
        );

        entityManager.persistAndFlush(customer);
        entityManager.clear();
    }

    @Nested
    @DisplayName("Inherited JpaRepository methods")
    class InheritedMethods {

        @Test
        @DisplayName("Finds customer by existing ID")
        void findById_existingId_returnsCustomer() {
            Optional<Customer> result = customerRepository.findById(customer.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getFirstName()).isEqualTo("Ion");
            assertThat(result.get().getLastName()).isEqualTo("Popescu");
        }

        @Test
        @DisplayName("Returns empty Optional for unknown ID")
        void findById_unknownId_returnsEmpty() {
            Optional<Customer> result = customerRepository.findById(Long.MAX_VALUE);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Finds all customers")
        void findAll_existingCustomer_returnsList() {
            List<Customer> customers = customerRepository.findAll();

            assertThat(customers).hasSize(1);
            assertThat(customers.getFirst().getPhone()).isEqualTo("0712345678");
        }

        @Test
        @DisplayName("Saves a new customer")
        void save_newCustomer_persistsCustomer() {
            User secondUser = new User(
                    "second@test.com",
                    "password123",
                    Role.CUSTOMER
            );

            entityManager.persist(secondUser);

            Customer secondCustomer = new Customer(
                    secondUser,
                    "Maria",
                    "Ionescu",
                    "0798765432"
            );

            Customer savedCustomer = customerRepository.saveAndFlush(secondCustomer);

            assertThat(savedCustomer.getId()).isNotNull();
            assertThat(customerRepository.findAll()).hasSize(2);
        }

        @Test
        @DisplayName("Deletes customer by ID")
        void deleteById_existingCustomer_removesCustomer() {
            Long customerId = customer.getId();

            customerRepository.deleteById(customerId);
            customerRepository.flush();

            assertThat(customerRepository.findById(customerId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByPhone")
    class ExistsByPhone {

        @Test
        @DisplayName("Returns true when phone exists")
        void existsByPhone_existingPhone_returnsTrue() {
            boolean result = customerRepository.existsByPhone("0712345678");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Returns false when phone does not exist")
        void existsByPhone_unknownPhone_returnsFalse() {
            boolean result = customerRepository.existsByPhone("0700000000");

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByUserId")
    class ExistsByUserId {

        @Test
        @DisplayName("Returns true when user has customer profile")
        void existsByUserId_existingUser_returnsTrue() {
            boolean result = customerRepository.existsByUserId(
                    customer.getUser().getId()
            );

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Returns false when user has no customer profile")
        void existsByUserId_unknownUser_returnsFalse() {
            boolean result = customerRepository.existsByUserId(Long.MAX_VALUE);

            assertThat(result).isFalse();
        }
    }
}
