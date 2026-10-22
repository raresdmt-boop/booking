
package com.barbershop.booking.barbers.services;

import com.barbershop.booking.barbers.dtos.BarberCreateRequest;
import com.barbershop.booking.barbers.dtos.BarberResponse;
import com.barbershop.booking.barbers.dtos.BarberUpdateRequest;
import com.barbershop.booking.barbers.exceptions.BarberNotFound;
import com.barbershop.booking.barbers.exceptions.BarberUserAlreadyExists;
import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.barbers.repositories.BarberRepository;
import com.barbershop.booking.support.BarberFixtures;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BarberCommandServiceImpl unit tests")
class BarberCommandServiceImplTest {

    @Mock
    private BarberRepository barberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BarberCommandServiceImpl service;

    @Captor
    private ArgumentCaptor<Barber> barberCaptor;

    @Nested
    @DisplayName("addBarber")
    class AddBarber {

        @Test
        @DisplayName("Creates barber successfully")
        void addBarber_validRequest_returnsResponse() {
            User user = UserFixtures.persisted();

            BarberCreateRequest request = new BarberCreateRequest(
                    UserFixtures.KNOWN_ID,
                    "Andrei",
                    "Ionescu",
                    "0723456789"
            );

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(user));

            when(barberRepository.save(any(Barber.class)))
                    .thenAnswer(invocation -> {
                        Barber barber = invocation.getArgument(0);
                        ReflectionTestUtils.setField(
                                barber, "id", BarberFixtures.KNOWN_ID
                        );
                        return barber;
                    });

            BarberResponse response = service.addBarber(request);

            verify(barberRepository).save(barberCaptor.capture());

            Barber savedBarber = barberCaptor.getValue();

            assertThat(savedBarber.getFirstName()).isEqualTo("Andrei");
            assertThat(savedBarber.getLastName()).isEqualTo("Ionescu");
            assertThat(savedBarber.getPhone()).isEqualTo("0723456789");
            assertThat(savedBarber.getUser()).isSameAs(user);
            assertThat(savedBarber.isActive()).isTrue();

            assertThat(response.id()).isEqualTo(BarberFixtures.KNOWN_ID);
            assertThat(response.userId()).isEqualTo(UserFixtures.KNOWN_ID);
            assertThat(response.active()).isTrue();

            verify(barberRepository).existsByUserId(UserFixtures.KNOWN_ID);
        }

        @Test
        @DisplayName("Throws UserNotFound when user does not exist")
        void addBarber_unknownUser_throwsException() {
            BarberCreateRequest request = new BarberCreateRequest(
                    UserFixtures.OTHER_ID,
                    "Andrei",
                    "Ionescu",
                    "0723456789"
            );

            when(userRepository.findById(UserFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.addBarber(request))
                    .isInstanceOf(UserNotFound.class);

            verifyNoInteractions(barberRepository);
        }

        @Test
        @DisplayName("Throws BarberUserAlreadyExists when user has a barber profile")
        void addBarber_existingProfile_throwsException() {
            BarberCreateRequest request = new BarberCreateRequest(
                    UserFixtures.KNOWN_ID,
                    "Andrei",
                    "Ionescu",
                    "0723456789"
            );

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(UserFixtures.persisted()));

            when(barberRepository.existsByUserId(UserFixtures.KNOWN_ID))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.addBarber(request))
                    .isInstanceOf(BarberUserAlreadyExists.class);

            verify(barberRepository, never()).save(any(Barber.class));
        }
    }

    @Nested
    @DisplayName("updateBarber")
    class UpdateBarber {

        @Test
        @DisplayName("Updates barber fields successfully")
        void updateBarber_validRequest_updatesFields() {
            Barber barber = BarberFixtures.persisted();

            BarberUpdateRequest request = new BarberUpdateRequest(
                    "Mihai",
                    "Popescu",
                    "0798765432",
                    null
            );

            when(barberRepository.findById(BarberFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(barber));

            BarberResponse response = service.updateBarber(
                    BarberFixtures.KNOWN_ID, request
            );

            assertThat(response.firstName()).isEqualTo("Mihai");
            assertThat(response.lastName()).isEqualTo("Popescu");
            assertThat(response.phone()).isEqualTo("0798765432");
            assertThat(response.active()).isTrue();

            verify(barberRepository, never()).save(any(Barber.class));
        }

        @Test
        @DisplayName("Keeps existing values when fields are null")
        void updateBarber_nullFields_keepsExistingValues() {
            Barber barber = BarberFixtures.persisted();

            BarberUpdateRequest request = new BarberUpdateRequest(
                    null, null, null, null
            );

            when(barberRepository.findById(BarberFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(barber));

            BarberResponse response = service.updateBarber(
                    BarberFixtures.KNOWN_ID, request
            );

            assertThat(response.firstName()).isEqualTo("Andrei");
            assertThat(response.lastName()).isEqualTo("Ionescu");
            assertThat(response.phone()).isEqualTo("0723456789");
            assertThat(response.active()).isTrue();

            verify(barberRepository, never()).save(any(Barber.class));
        }

        @Test
        @DisplayName("Ignores blank text fields")
        void updateBarber_blankFields_keepsExistingValues() {
            Barber barber = BarberFixtures.persisted();

            BarberUpdateRequest request = new BarberUpdateRequest(
                    " ", "", " ", null
            );

            when(barberRepository.findById(BarberFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(barber));

            BarberResponse response = service.updateBarber(
                    BarberFixtures.KNOWN_ID, request
            );

            assertThat(response.firstName()).isEqualTo("Andrei");
            assertThat(response.lastName()).isEqualTo("Ionescu");
            assertThat(response.phone()).isEqualTo("0723456789");
        }

        @Test
        @DisplayName("Deactivates barber")
        void updateBarber_activeFalse_deactivatesBarber() {
            Barber barber = BarberFixtures.persisted();

            BarberUpdateRequest request = new BarberUpdateRequest(
                    null, null, null, false
            );

            when(barberRepository.findById(BarberFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(barber));

            BarberResponse response = service.updateBarber(
                    BarberFixtures.KNOWN_ID, request
            );

            assertThat(response.active()).isFalse();
            assertThat(barber.isActive()).isFalse();
        }

        @Test
        @DisplayName("Activates barber")
        void updateBarber_activeTrue_activatesBarber() {
            Barber barber = BarberFixtures.persisted();
            barber.setActive(false);

            BarberUpdateRequest request = new BarberUpdateRequest(
                    null, null, null, true
            );

            when(barberRepository.findById(BarberFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(barber));

            BarberResponse response = service.updateBarber(
                    BarberFixtures.KNOWN_ID, request
            );

            assertThat(response.active()).isTrue();
            assertThat(barber.isActive()).isTrue();
        }

        @Test
        @DisplayName("Throws BarberNotFound when ID does not exist")
        void updateBarber_unknownId_throwsException() {
            BarberUpdateRequest request = new BarberUpdateRequest(
                    "Mihai", null, null, null
            );

            when(barberRepository.findById(BarberFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateBarber(
                    BarberFixtures.OTHER_ID, request
            )).isInstanceOf(BarberNotFound.class);

            verify(barberRepository, never()).save(any(Barber.class));
        }
    }

    @Nested
    @DisplayName("deleteBarber")
    class DeleteBarber {

        @Test
        @DisplayName("Deletes existing barber and returns response")
        void deleteBarber_existingId_returnsDeletedBarber() {
            Barber barber = BarberFixtures.persisted();

            when(barberRepository.findById(BarberFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(barber));

            BarberResponse response = service.deleteBarber(
                    BarberFixtures.KNOWN_ID
            );

            assertThat(response.id()).isEqualTo(BarberFixtures.KNOWN_ID);
            assertThat(response.firstName()).isEqualTo("Andrei");
            assertThat(response.phone()).isEqualTo("0723456789");

            verify(barberRepository).delete(barber);
        }

        @Test
        @DisplayName("Throws BarberNotFound when ID does not exist")
        void deleteBarber_unknownId_throwsException() {
            when(barberRepository.findById(BarberFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteBarber(
                    BarberFixtures.OTHER_ID
            )).isInstanceOf(BarberNotFound.class);

            verify(barberRepository, never()).delete(any(Barber.class));
        }
    }
}
