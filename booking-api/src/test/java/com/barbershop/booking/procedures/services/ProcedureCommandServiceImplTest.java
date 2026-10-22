
package com.barbershop.booking.procedures.services;

import com.barbershop.booking.barbers.exceptions.BarberNotFound;
import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.barbers.repositories.BarberRepository;
import com.barbershop.booking.procedures.dtos.ProcedureCreateRequest;
import com.barbershop.booking.procedures.dtos.ProcedureResponse;
import com.barbershop.booking.procedures.dtos.ProcedureUpdateRequest;
import com.barbershop.booking.procedures.exceptions.ProcedureNameAlreadyExists;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcedureCommandServiceImpl tests")
class ProcedureCommandServiceImplTest {

    @Mock
    private ProcedureRepository procedureRepository;

    @Mock
    private BarberRepository barberRepository;

    @InjectMocks
    private ProcedureCommandServiceImpl procedureCommandService;

    @Captor
    private ArgumentCaptor<Procedure> procedureCaptor;

    private Procedure procedure;
    private Barber barber;

    @BeforeEach
    void setUp() {
        procedure = ProcedureFixtures.persisted();
        barber = BarberFixtures.persisted();
    }

    @Nested
    @DisplayName("Add procedure")
    class AddProcedure {

        @Test
        @DisplayName("Should add procedure successfully")
        void shouldAddProcedureSuccessfully() {
            ProcedureCreateRequest request = new ProcedureCreateRequest(
                    BarberFixtures.KNOWN_ID,
                    "Haircut",
                    "Men's haircut",
                    30,
                    new BigDecimal("50.00")
            );

            when(barberRepository.findById(BarberFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(barber));

            when(procedureRepository.existsByBarberIdAndName(
                    BarberFixtures.KNOWN_ID, "Haircut"))
                    .thenReturn(false);

            when(procedureRepository.save(any(Procedure.class)))
                    .thenAnswer(invocation -> {
                        Procedure saved = invocation.getArgument(0);
                        org.springframework.test.util.ReflectionTestUtils
                                .setField(saved, "id", ProcedureFixtures.KNOWN_ID);
                        return saved;
                    });

            ProcedureResponse response = procedureCommandService.addProcedure(request);

            verify(procedureRepository).save(procedureCaptor.capture());

            Procedure captured = procedureCaptor.getValue();

            assertThat(captured.getName()).isEqualTo("Haircut");
            assertThat(captured.getDescription()).isEqualTo("Men's haircut");
            assertThat(captured.getDurationMinutes()).isEqualTo(30);
            assertThat(captured.getPrice()).isEqualByComparingTo("50.00");
            assertThat(captured.getBarber()).isSameAs(barber);
            assertThat(captured.isActive()).isTrue();

            assertThat(response.id()).isEqualTo(ProcedureFixtures.KNOWN_ID);
            assertThat(response.barberId()).isEqualTo(BarberFixtures.KNOWN_ID);
            assertThat(response.name()).isEqualTo("Haircut");

            verify(barberRepository).findById(BarberFixtures.KNOWN_ID);
            verify(procedureRepository).existsByBarberIdAndName(
                    BarberFixtures.KNOWN_ID, "Haircut");
        }

        @Test
        @DisplayName("Should throw when barber does not exist")
        void shouldThrowWhenBarberDoesNotExist() {
            ProcedureCreateRequest request = new ProcedureCreateRequest(
                    BarberFixtures.OTHER_ID,
                    "Haircut",
                    "Men's haircut",
                    30,
                    new BigDecimal("50.00")
            );

            when(barberRepository.findById(BarberFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> procedureCommandService.addProcedure(request))
                    .isInstanceOf(BarberNotFound.class);

            verify(procedureRepository, never())
                    .existsByBarberIdAndName(any(), any());
            verify(procedureRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when procedure name already exists")
        void shouldThrowWhenNameAlreadyExists() {
            ProcedureCreateRequest request = new ProcedureCreateRequest(
                    BarberFixtures.KNOWN_ID,
                    "Haircut",
                    "Men's haircut",
                    30,
                    new BigDecimal("50.00")
            );

            when(barberRepository.findById(BarberFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(barber));

            when(procedureRepository.existsByBarberIdAndName(
                    BarberFixtures.KNOWN_ID, "Haircut"))
                    .thenReturn(true);

            assertThatThrownBy(() -> procedureCommandService.addProcedure(request))
                    .isInstanceOf(ProcedureNameAlreadyExists.class);

            verify(procedureRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update procedure")
    class UpdateProcedure {

        @Test
        @DisplayName("Should update all provided fields")
        void shouldUpdateAllProvidedFields() {
            ProcedureUpdateRequest request = new ProcedureUpdateRequest(
                    "Premium Haircut",
                    "Haircut and styling",
                    45,
                    new BigDecimal("80.00"),
                    false
            );

            when(procedureRepository.findById(ProcedureFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(procedure));

            when(procedureRepository.existsByBarberIdAndName(
                    BarberFixtures.KNOWN_ID, "Premium Haircut"))
                    .thenReturn(false);

            ProcedureResponse response = procedureCommandService.updateProcedure(
                    ProcedureFixtures.KNOWN_ID, request);

            assertThat(response.name()).isEqualTo("Premium Haircut");
            assertThat(response.description()).isEqualTo("Haircut and styling");
            assertThat(response.durationMinutes()).isEqualTo(45);
            assertThat(response.price()).isEqualByComparingTo("80.00");
            assertThat(response.active()).isFalse();

            verify(procedureRepository).existsByBarberIdAndName(
                    BarberFixtures.KNOWN_ID, "Premium Haircut");
            verify(procedureRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should preserve existing values when fields are null")
        void shouldPreserveExistingValuesWhenFieldsAreNull() {
            ProcedureUpdateRequest request = new ProcedureUpdateRequest(
                    null, null, null, null, null
            );

            when(procedureRepository.findById(ProcedureFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(procedure));

            ProcedureResponse response = procedureCommandService.updateProcedure(
                    ProcedureFixtures.KNOWN_ID, request);

            assertThat(response.name()).isEqualTo("Haircut");
            assertThat(response.description()).isEqualTo("Men's haircut");
            assertThat(response.durationMinutes()).isEqualTo(30);
            assertThat(response.price()).isEqualByComparingTo("50.00");
            assertThat(response.active()).isTrue();

            verify(procedureRepository, never())
                    .existsByBarberIdAndName(any(), any());
            verify(procedureRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update only price")
        void shouldUpdateOnlyPrice() {
            ProcedureUpdateRequest request = new ProcedureUpdateRequest(
                    null, null, null, new BigDecimal("65.00"), null
            );

            when(procedureRepository.findById(ProcedureFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(procedure));

            ProcedureResponse response = procedureCommandService.updateProcedure(
                    ProcedureFixtures.KNOWN_ID, request);

            assertThat(response.price()).isEqualByComparingTo("65.00");
            assertThat(response.name()).isEqualTo("Haircut");
            assertThat(response.durationMinutes()).isEqualTo(30);

            verify(procedureRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should not check duplicate when name remains unchanged")
        void shouldNotCheckDuplicateWhenNameUnchanged() {
            ProcedureUpdateRequest request = new ProcedureUpdateRequest(
                    "Haircut", null, null, null, null
            );

            when(procedureRepository.findById(ProcedureFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(procedure));

            ProcedureResponse response = procedureCommandService.updateProcedure(
                    ProcedureFixtures.KNOWN_ID, request);

            assertThat(response.name()).isEqualTo("Haircut");

            verify(procedureRepository, never())
                    .existsByBarberIdAndName(any(), any());
        }

        @Test
        @DisplayName("Should ignore blank procedure name")
        void shouldIgnoreBlankProcedureName() {
            ProcedureUpdateRequest request = new ProcedureUpdateRequest(
                    "   ", null, null, null, null
            );

            when(procedureRepository.findById(ProcedureFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(procedure));

            ProcedureResponse response = procedureCommandService.updateProcedure(
                    ProcedureFixtures.KNOWN_ID, request);

            assertThat(response.name()).isEqualTo("Haircut");

            verify(procedureRepository, never())
                    .existsByBarberIdAndName(any(), any());
        }

        @Test
        @DisplayName("Should throw when new name already exists")
        void shouldThrowWhenNewNameAlreadyExists() {
            ProcedureUpdateRequest request = new ProcedureUpdateRequest(
                    "Beard Trim", null, null, null, null
            );

            when(procedureRepository.findById(ProcedureFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(procedure));

            when(procedureRepository.existsByBarberIdAndName(
                    BarberFixtures.KNOWN_ID, "Beard Trim"))
                    .thenReturn(true);

            assertThatThrownBy(() -> procedureCommandService.updateProcedure(
                    ProcedureFixtures.KNOWN_ID, request))
                    .isInstanceOf(ProcedureNameAlreadyExists.class);

            assertThat(procedure.getName()).isEqualTo("Haircut");
            verify(procedureRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when procedure does not exist")
        void shouldThrowWhenProcedureDoesNotExist() {
            ProcedureUpdateRequest request = new ProcedureUpdateRequest(
                    "Premium Haircut", null, null, null, null
            );

            when(procedureRepository.findById(ProcedureFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> procedureCommandService.updateProcedure(
                    ProcedureFixtures.OTHER_ID, request))
                    .isInstanceOf(ProcedureNotFound.class);

            verify(procedureRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should deactivate procedure")
        void shouldDeactivateProcedure() {
            ProcedureUpdateRequest request = new ProcedureUpdateRequest(
                    null, null, null, null, false
            );

            when(procedureRepository.findById(ProcedureFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(procedure));

            ProcedureResponse response = procedureCommandService.updateProcedure(
                    ProcedureFixtures.KNOWN_ID, request);

            assertThat(response.active()).isFalse();
            assertThat(procedure.isActive()).isFalse();

            verify(procedureRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete procedure")
    class DeleteProcedure {

        @Test
        @DisplayName("Should delete procedure successfully")
        void shouldDeleteProcedureSuccessfully() {
            when(procedureRepository.findById(ProcedureFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(procedure));

            ProcedureResponse response = procedureCommandService.deleteProcedure(
                    ProcedureFixtures.KNOWN_ID);

            assertThat(response.id()).isEqualTo(ProcedureFixtures.KNOWN_ID);
            assertThat(response.name()).isEqualTo("Haircut");
            assertThat(response.barberId()).isEqualTo(BarberFixtures.KNOWN_ID);

            verify(procedureRepository).delete(procedure);
        }

        @Test
        @DisplayName("Should throw when procedure does not exist")
        void shouldThrowWhenProcedureDoesNotExist() {
            when(procedureRepository.findById(ProcedureFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> procedureCommandService.deleteProcedure(
                    ProcedureFixtures.OTHER_ID))
                    .isInstanceOf(ProcedureNotFound.class);

            verify(procedureRepository, never()).delete(any());
        }
    }
}
