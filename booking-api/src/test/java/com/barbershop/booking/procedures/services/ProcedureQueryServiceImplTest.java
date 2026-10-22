
package com.barbershop.booking.procedures.services;

import com.barbershop.booking.procedures.dtos.ProcedureResponse;
import com.barbershop.booking.procedures.exceptions.ProcedureNotFound;
import com.barbershop.booking.procedures.models.Procedure;
import com.barbershop.booking.procedures.repository.ProcedureRepository;
import com.barbershop.booking.support.BarberFixtures;
import com.barbershop.booking.support.ProcedureFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcedureQueryServiceImpl tests")
class ProcedureQueryServiceImplTest {

    @Mock
    private ProcedureRepository procedureRepository;

    @InjectMocks
    private ProcedureQueryServiceImpl procedureQueryService;

    private Procedure procedure;

    @BeforeEach
    void setUp() {
        procedure = ProcedureFixtures.persisted();
    }

    @Nested
    @DisplayName("Get procedure by ID")
    class GetProcedureById {

        @Test
        @DisplayName("Should return procedure when ID exists")
        void shouldReturnProcedureWhenIdExists() {
            when(procedureRepository.findById(ProcedureFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(procedure));

            ProcedureResponse response = procedureQueryService.getProcedureById(
                    ProcedureFixtures.KNOWN_ID
            );

            assertThat(response.id()).isEqualTo(ProcedureFixtures.KNOWN_ID);
            assertThat(response.barberId()).isEqualTo(BarberFixtures.KNOWN_ID);
            assertThat(response.name()).isEqualTo("Haircut");
            assertThat(response.description()).isEqualTo("Men's haircut");
            assertThat(response.durationMinutes()).isEqualTo(30);
            assertThat(response.price()).isEqualByComparingTo("50.00");
            assertThat(response.active()).isTrue();

            verify(procedureRepository).findById(ProcedureFixtures.KNOWN_ID);
        }

        @Test
        @DisplayName("Should throw when procedure does not exist")
        void shouldThrowWhenProcedureDoesNotExist() {
            when(procedureRepository.findById(ProcedureFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> procedureQueryService.getProcedureById(
                    ProcedureFixtures.OTHER_ID
            )).isInstanceOf(ProcedureNotFound.class);

            verify(procedureRepository).findById(ProcedureFixtures.OTHER_ID);
        }
    }

    @Nested
    @DisplayName("Get all procedures")
    class GetProcedures {

        @Test
        @DisplayName("Should return all procedures")
        void shouldReturnAllProcedures() {
            Procedure secondProcedure = new Procedure(
                    "Beard Trim",
                    "Beard trimming",
                    20,
                    new BigDecimal("30.00"),
                    BarberFixtures.persisted()
            );

            when(procedureRepository.findAll())
                    .thenReturn(List.of(procedure, secondProcedure));

            List<ProcedureResponse> response = procedureQueryService.getProcedures();

            assertThat(response).hasSize(2);
            assertThat(response)
                    .extracting(ProcedureResponse::name)
                    .containsExactly("Haircut", "Beard Trim");

            verify(procedureRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no procedures exist")
        void shouldReturnEmptyListWhenNoProceduresExist() {
            when(procedureRepository.findAll())
                    .thenReturn(List.of());

            List<ProcedureResponse> response = procedureQueryService.getProcedures();

            assertThat(response).isEmpty();

            verify(procedureRepository).findAll();
        }

        @Test
        @DisplayName("Should include inactive procedures")
        void shouldIncludeInactiveProcedures() {
            procedure.setActive(false);

            when(procedureRepository.findAll())
                    .thenReturn(List.of(procedure));

            List<ProcedureResponse> response = procedureQueryService.getProcedures();

            assertThat(response).hasSize(1);
            assertThat(response.get(0).active()).isFalse();

            verify(procedureRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Get procedures by barber ID")
    class GetProceduresByBarberId {

        @Test
        @DisplayName("Should return procedures belonging to barber")
        void shouldReturnProceduresBelongingToBarber() {
            Procedure secondProcedure = new Procedure(
                    "Beard Trim",
                    "Beard trimming",
                    20,
                    new BigDecimal("30.00"),
                    BarberFixtures.persisted()
            );

            when(procedureRepository.findByBarberId(BarberFixtures.KNOWN_ID))
                    .thenReturn(List.of(procedure, secondProcedure));

            List<ProcedureResponse> response =
                    procedureQueryService.getProceduresByBarberId(
                            BarberFixtures.KNOWN_ID
                    );

            assertThat(response).hasSize(2);
            assertThat(response)
                    .extracting(ProcedureResponse::name)
                    .containsExactly("Haircut", "Beard Trim");

            assertThat(response)
                    .extracting(ProcedureResponse::barberId)
                    .containsOnly(BarberFixtures.KNOWN_ID);

            verify(procedureRepository)
                    .findByBarberId(BarberFixtures.KNOWN_ID);
        }

        @Test
        @DisplayName("Should return empty list when barber has no procedures")
        void shouldReturnEmptyListWhenBarberHasNoProcedures() {
            when(procedureRepository.findByBarberId(BarberFixtures.OTHER_ID))
                    .thenReturn(List.of());

            List<ProcedureResponse> response =
                    procedureQueryService.getProceduresByBarberId(
                            BarberFixtures.OTHER_ID
                    );

            assertThat(response).isEmpty();

            verify(procedureRepository)
                    .findByBarberId(BarberFixtures.OTHER_ID);
        }

        @Test
        @DisplayName("Should include inactive procedures for barber")
        void shouldIncludeInactiveProceduresForBarber() {
            procedure.setActive(false);

            when(procedureRepository.findByBarberId(BarberFixtures.KNOWN_ID))
                    .thenReturn(List.of(procedure));

            List<ProcedureResponse> response =
                    procedureQueryService.getProceduresByBarberId(
                            BarberFixtures.KNOWN_ID
                    );

            assertThat(response).hasSize(1);
            assertThat(response.get(0).active()).isFalse();

            verify(procedureRepository)
                    .findByBarberId(BarberFixtures.KNOWN_ID);
        }
    }
}
