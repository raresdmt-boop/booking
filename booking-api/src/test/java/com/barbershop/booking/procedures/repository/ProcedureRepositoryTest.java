
package com.barbershop.booking.procedures.repository;

import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.procedures.models.Procedure;
import com.barbershop.booking.support.BarberFixtures;
import com.barbershop.booking.support.ProcedureFixtures;
import com.barbershop.booking.support.UserFixtures;
import com.barbershop.booking.users.models.Role;
import com.barbershop.booking.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(showSql = false)
@DisplayName("ProcedureRepository tests")
class ProcedureRepositoryTest {

    @Autowired
    private ProcedureRepository procedureRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Barber barber;

    @BeforeEach
    void setUp() {
        Barber fixture = BarberFixtures.barber();

        entityManager.persist(fixture.getUser());

        barber = entityManager.persistAndFlush(fixture);
        entityManager.clear();
    }

    private Procedure createProcedure(String name, Barber barber) {
        return new Procedure(
                name,
                "Men's haircut",
                30,
                new BigDecimal("50.00"),
                barber
        );
    }

    private Barber createAnotherBarber() {
        User user = new User(
                "anotherbarber@gmail.com",
                "hashedPassword123",
                Role.BARBER
        );

        entityManager.persist(user);

        Barber anotherBarber = new Barber(
                "Mihai",
                "Popescu",
                "0734567890",
                user
        );

        return entityManager.persistAndFlush(anotherBarber);
    }

    @Nested
    @DisplayName("Save procedure")
    class SaveProcedure {

        @Test
        @DisplayName("Should save procedure successfully")
        void shouldSaveProcedureSuccessfully() {
            Procedure procedure = createProcedure("Haircut", barber);

            Procedure savedProcedure = procedureRepository.saveAndFlush(procedure);

            assertThat(savedProcedure.getId()).isNotNull();
            assertThat(savedProcedure.getName()).isEqualTo("Haircut");
            assertThat(savedProcedure.getPrice())
                    .isEqualByComparingTo("50.00");
            assertThat(savedProcedure.isActive()).isTrue();
            assertThat(savedProcedure.getBarber().getId())
                    .isEqualTo(barber.getId());
        }
    }

    @Nested
    @DisplayName("Find procedure by ID")
    class FindProcedureById {

        @Test
        @DisplayName("Should return procedure when ID exists")
        void shouldReturnProcedureWhenIdExists() {
            Procedure procedure = entityManager.persistAndFlush(
                    createProcedure("Haircut", barber)
            );

            entityManager.clear();

            Optional<Procedure> result =
                    procedureRepository.findById(procedure.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Haircut");
        }

        @Test
        @DisplayName("Should return empty when ID does not exist")
        void shouldReturnEmptyWhenIdDoesNotExist() {
            Optional<Procedure> result =
                    procedureRepository.findById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Exists by barber ID and name")
    class ExistsByBarberIdAndName {

        @Test
        @DisplayName("Should return true when procedure exists")
        void shouldReturnTrueWhenProcedureExists() {
            entityManager.persistAndFlush(
                    createProcedure("Haircut", barber)
            );

            boolean result = procedureRepository.existsByBarberIdAndName(
                    barber.getId(),
                    "Haircut"
            );

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false when procedure name does not exist")
        void shouldReturnFalseWhenNameDoesNotExist() {
            entityManager.persistAndFlush(
                    createProcedure("Haircut", barber)
            );

            boolean result = procedureRepository.existsByBarberIdAndName(
                    barber.getId(),
                    "Beard Trim"
            );

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false when procedure belongs to another barber")
        void shouldReturnFalseWhenProcedureBelongsToAnotherBarber() {
            Barber anotherBarber = createAnotherBarber();

            entityManager.persistAndFlush(
                    createProcedure("Haircut", barber)
            );

            boolean result = procedureRepository.existsByBarberIdAndName(
                    anotherBarber.getId(),
                    "Haircut"
            );

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Find procedures by barber ID")
    class FindByBarberId {

        @Test
        @DisplayName("Should return all procedures belonging to barber")
        void shouldReturnProceduresForBarber() {
            entityManager.persist(
                    createProcedure("Haircut", barber)
            );

            entityManager.persistAndFlush(
                    createProcedure("Beard Trim", barber)
            );

            entityManager.clear();

            List<Procedure> result =
                    procedureRepository.findByBarberId(barber.getId());

            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(Procedure::getName)
                    .containsExactlyInAnyOrder("Haircut", "Beard Trim");
        }

        @Test
        @DisplayName("Should return empty list when barber has no procedures")
        void shouldReturnEmptyListWhenBarberHasNoProcedures() {
            List<Procedure> result =
                    procedureRepository.findByBarberId(barber.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should include inactive procedures")
        void shouldIncludeInactiveProcedures() {
            Procedure procedure = createProcedure("Haircut", barber);
            procedure.setActive(false);

            entityManager.persistAndFlush(procedure);
            entityManager.clear();

            List<Procedure> result =
                    procedureRepository.findByBarberId(barber.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("Delete procedure")
    class DeleteProcedure {

        @Test
        @DisplayName("Should delete existing procedure")
        void shouldDeleteExistingProcedure() {
            Procedure procedure = entityManager.persistAndFlush(
                    createProcedure("Haircut", barber)
            );

            Long procedureId = procedure.getId();

            procedureRepository.delete(procedure);
            procedureRepository.flush();
            entityManager.clear();

            assertThat(procedureRepository.findById(procedureId))
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("Unique constraints")
    class UniqueConstraints {

        @Test
        @DisplayName("Should reject duplicate procedure name for same barber")
        void shouldRejectDuplicateNameForSameBarber() {
            entityManager.persistAndFlush(
                    createProcedure("Haircut", barber)
            );

            Procedure duplicate = createProcedure("Haircut", barber);

            assertThatThrownBy(() ->
                    entityManager.persistAndFlush(duplicate)
            ).isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Should allow same procedure name for different barbers")
        void shouldAllowSameNameForDifferentBarbers() {
            Barber anotherBarber = createAnotherBarber();

            Procedure first = entityManager.persistAndFlush(
                    createProcedure("Haircut", barber)
            );

            Procedure second = entityManager.persistAndFlush(
                    createProcedure("Haircut", anotherBarber)
            );

            assertThat(first.getId()).isNotEqualTo(second.getId());
        }
    }
}
