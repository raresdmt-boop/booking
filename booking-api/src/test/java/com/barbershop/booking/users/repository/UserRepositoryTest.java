package com.barbershop.booking.users.repository;

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
@DisplayName("UserRepository integration tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User firstUser;
    private User secondUser;

    @BeforeEach
    void setUp() {
        firstUser = new User("first@gmail.com", "password123", Role.CUSTOMER);
        secondUser = new User("second@gmail.com", "password456", Role.BARBER);

        entityManager.persist(firstUser);
        entityManager.persist(secondUser);
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("Inherited JpaRepository methods")
    class Inherited {

        @Test
        @DisplayName("findById returns an existing user")
        void findById_existingUser_returnsUser() {
            Optional<User> result = userRepository.findById(firstUser.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("first@gmail.com");
        }

        @Test
        @DisplayName("findById returns empty for unknown ID")
        void findById_unknownId_returnsEmpty() {
            Optional<User> result = userRepository.findById(-1L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findAll returns all users")
        void findAll_returnsAllUsers() {
            List<User> result = userRepository.findAll();

            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(User::getEmail)
                    .containsExactlyInAnyOrder("first@gmail.com", "second@gmail.com");
        }

        @Test
        @DisplayName("save persists a new user")
        void save_newUser_persistsUser() {
            User newUser = new User("third@gmail.com", "password789", Role.CUSTOMER);

            User saved = userRepository.save(newUser);
            entityManager.flush();
            entityManager.clear();

            assertThat(saved.getId()).isNotNull();
            assertThat(userRepository.findById(saved.getId())).isPresent();
        }

        @Test
        @DisplayName("count returns number of users")
        void count_returnsNumberOfUsers() {
            long result = userRepository.count();

            assertThat(result).isEqualTo(2);
        }

        @Test
        @DisplayName("delete removes an existing user")
        void delete_existingUser_removesUser() {
            userRepository.deleteById(firstUser.getId());
            entityManager.flush();
            entityManager.clear();

            assertThat(userRepository.findById(firstUser.getId())).isEmpty();
            assertThat(userRepository.count()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Derived query methods")
    class Derived {

        @Test
        @DisplayName("existsByEmail returns true for existing email")
        void existsByEmail_existingEmail_returnsTrue() {
            boolean result = userRepository.existsByEmail("first@gmail.com");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("existsByEmail returns false for unknown email")
        void existsByEmail_unknownEmail_returnsFalse() {
            boolean result = userRepository.existsByEmail("unknown@gmail.com");

            assertThat(result).isFalse();
        }
    }
}