
package com.barbershop.booking.barbers.repositories;

import com.barbershop.booking.barbers.models.Barber;
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
@DisplayName("BarberRepository integration tests")
class BarberRepositoryTest {

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Barber barber;
    private Long userId;

    @BeforeEach
    void setUp() {
        User user = new User(
                "barber@test.com",
                "password123",
                Role.BARBER
        );

        entityManager.persist(user);
        userId = user.getId();

        barber = new Barber(
                "Andrei",
                "Popescu",
                "0712345678",
                user
        );

        entityManager.persistAndFlush(barber);
        entityManager.clear();
    }

    @Nested
    @DisplayName("Inherited JpaRepository methods")
    class InheritedMethods {

        @Test
        @DisplayName("Finds barber by existing ID")
        void findById_existingId_returnsBarber() {
            Optional<Barber> result = barberRepository.findById(barber.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getFirstName()).isEqualTo("Andrei");
            assertThat(result.get().getLastName()).isEqualTo("Popescu");
            assertThat(result.get().isActive()).isTrue();
        }

        @Test
        @DisplayName("Returns empty Optional for unknown ID")
        void findById_unknownId_returnsEmpty() {
            Optional<Barber> result = barberRepository.findById(Long.MAX_VALUE);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Finds all barbers")
        void findAll_existingBarber_returnsList() {
            List<Barber> barbers = barberRepository.findAll();

            assertThat(barbers).hasSize(1);
            assertThat(barbers.getFirst().getPhone()).isEqualTo("0712345678");
        }

        @Test
        @DisplayName("Saves a new barber")
        void save_newBarber_persistsBarber() {
            User secondUser = new User(
                    "secondbarber@test.com",
                    "password123",
                    Role.BARBER
            );

            entityManager.persist(secondUser);

            Barber secondBarber = new Barber(
                    "Mihai",
                    "Ionescu",
                    "0798765432",
                    secondUser
            );

            Barber savedBarber = barberRepository.saveAndFlush(secondBarber);

            assertThat(savedBarber.getId()).isNotNull();
            assertThat(savedBarber.isActive()).isTrue();
            assertThat(barberRepository.findAll()).hasSize(2);
        }

        @Test
        @DisplayName("Deletes barber by ID")
        void deleteById_existingBarber_removesBarber() {
            Long barberId = barber.getId();

            barberRepository.deleteById(barberId);
            barberRepository.flush();

            assertThat(barberRepository.findById(barberId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByUserId")
    class ExistsByUserId {

        @Test
        @DisplayName("Returns true when user has barber profile")
        void existsByUserId_existingUser_returnsTrue() {
            boolean result = barberRepository.existsByUserId(userId);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Returns false when user has no barber profile")
        void existsByUserId_unknownUser_returnsFalse() {
            boolean result = barberRepository.existsByUserId(Long.MAX_VALUE);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Returns false when existing user has no barber profile")
        void existsByUserId_userWithoutBarber_returnsFalse() {
            User userWithoutBarber = new User(
                    "nobarber@test.com",
                    "password123",
                    Role.BARBER
            );

            entityManager.persistAndFlush(userWithoutBarber);

            boolean result = barberRepository.existsByUserId(
                    userWithoutBarber.getId()
            );

            assertThat(result).isFalse();
        }
    }
}
