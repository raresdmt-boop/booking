
package com.barbershop.booking.barbers.services;

import com.barbershop.booking.barbers.dtos.BarberResponse;
import com.barbershop.booking.barbers.exceptions.BarberNotFound;
import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.barbers.repositories.BarberRepository;
import com.barbershop.booking.support.BarberFixtures;
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
@DisplayName("BarberQueryServiceImpl unit tests")
class BarberQueryServiceImplTest {

    @Mock
    private BarberRepository barberRepository;

    @InjectMocks
    private BarberQueryServiceImpl service;

    @Nested
    @DisplayName("getBarberById")
    class GetBarberById {

        @Test
        @DisplayName("Returns barber when ID exists")
        void getBarberById_existingId_returnsResponse() {
            Barber barber = BarberFixtures.persisted();

            when(barberRepository.findById(BarberFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(barber));

            BarberResponse response = service.getBarberById(
                    BarberFixtures.KNOWN_ID
            );

            assertThat(response.id()).isEqualTo(BarberFixtures.KNOWN_ID);
            assertThat(response.firstName()).isEqualTo("Andrei");
            assertThat(response.lastName()).isEqualTo("Ionescu");
            assertThat(response.phone()).isEqualTo("0723456789");
            assertThat(response.active()).isTrue();
            assertThat(response.createdAt()).isEqualTo(barber.getCreatedAt());

            verify(barberRepository).findById(BarberFixtures.KNOWN_ID);
        }

        @Test
        @DisplayName("Throws BarberNotFound when ID does not exist")
        void getBarberById_unknownId_throwsException() {
            when(barberRepository.findById(BarberFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getBarberById(
                    BarberFixtures.OTHER_ID
            )).isInstanceOf(BarberNotFound.class);

            verify(barberRepository).findById(BarberFixtures.OTHER_ID);
        }
    }

    @Nested
    @DisplayName("getBarbers")
    class GetBarbers {

        @Test
        @DisplayName("Returns all barbers mapped to responses")
        void getBarbers_existingBarbers_returnsResponses() {
            Barber firstBarber = BarberFixtures.persisted();

            Barber secondBarber = BarberFixtures.withId(
                    BarberFixtures.OTHER_ID
            );
            secondBarber.setFirstName("Mihai");
            secondBarber.setLastName("Popescu");
            secondBarber.setPhone("0798765432");

            when(barberRepository.findAll())
                    .thenReturn(List.of(firstBarber, secondBarber));

            List<BarberResponse> responses = service.getBarbers();

            assertThat(responses).hasSize(2);

            assertThat(responses)
                    .extracting(BarberResponse::firstName)
                    .containsExactly("Andrei", "Mihai");

            assertThat(responses)
                    .extracting(BarberResponse::phone)
                    .containsExactly("0723456789", "0798765432");

            verify(barberRepository).findAll();
        }

        @Test
        @DisplayName("Returns empty list when no barbers exist")
        void getBarbers_noBarbers_returnsEmptyList() {
            when(barberRepository.findAll()).thenReturn(List.of());

            List<BarberResponse> responses = service.getBarbers();

            assertThat(responses).isEmpty();

            verify(barberRepository).findAll();
        }

        @Test
        @DisplayName("Returns inactive barber correctly")
        void getBarbers_inactiveBarber_returnsActiveFalse() {
            Barber barber = BarberFixtures.persisted();
            barber.setActive(false);

            when(barberRepository.findAll())
                    .thenReturn(List.of(barber));

            List<BarberResponse> responses = service.getBarbers();

            assertThat(responses).hasSize(1);
            assertThat(responses.getFirst().active()).isFalse();
        }
    }
}
