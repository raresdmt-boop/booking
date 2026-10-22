
package com.barbershop.booking.users.services;

import com.barbershop.booking.support.UserFixtures;
import com.barbershop.booking.users.dtos.UserResponse;
import com.barbershop.booking.users.exceptions.UserExceptionConstants;
import com.barbershop.booking.users.exceptions.UserNotFound;
import com.barbershop.booking.users.models.Role;
import com.barbershop.booking.users.models.User;
import com.barbershop.booking.users.repository.UserRepository;
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
@DisplayName("UserQueryServiceImpl unit tests")
class UserQueryServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryServiceImpl service;

    @Nested
    @DisplayName("getUserById")
    class GetUserById {

        @Test
        @DisplayName("Returns user when ID exists")
        void getUserById_existingUser_returnsResponse() {
            User user = UserFixtures.persisted();

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(user));

            UserResponse response = service.getUserById(UserFixtures.KNOWN_ID);

            assertThat(response.id()).isEqualTo(UserFixtures.KNOWN_ID);
            assertThat(response.email()).isEqualTo("test@gmail.com");
            assertThat(response.role()).isEqualTo(Role.CUSTOMER);
            assertThat(response.createdAt()).isEqualTo(user.getCreatedAt());

            verify(userRepository).findById(UserFixtures.KNOWN_ID);
        }

        @Test
        @DisplayName("Throws UserNotFound when ID does not exist")
        void getUserById_unknownId_throwsException() {
            when(userRepository.findById(UserFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getUserById(UserFixtures.OTHER_ID))
                    .isInstanceOf(UserNotFound.class)
                    .hasMessage(UserExceptionConstants.USER_ID_NOT_FOUND);

            verify(userRepository).findById(UserFixtures.OTHER_ID);
        }
    }

    @Nested
    @DisplayName("getUsers")
    class GetUsers {

        @Test
        @DisplayName("Returns all users mapped to responses")
        void getUsers_existingUsers_returnsResponses() {
            User firstUser = UserFixtures.persisted();
            User secondUser = UserFixtures.withId(UserFixtures.OTHER_ID);
            secondUser.setEmail("second@gmail.com");
            secondUser.setRole(Role.BARBER);

            when(userRepository.findAll())
                    .thenReturn(List.of(firstUser, secondUser));

            List<UserResponse> responses = service.getUsers();

            assertThat(responses).hasSize(2);

            assertThat(responses)
                    .extracting(UserResponse::email)
                    .containsExactly("test@gmail.com", "second@gmail.com");

            assertThat(responses)
                    .extracting(UserResponse::role)
                    .containsExactly(Role.CUSTOMER, Role.BARBER);

            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Returns empty list when no users exist")
        void getUsers_noUsers_returnsEmptyList() {
            when(userRepository.findAll()).thenReturn(List.of());

            List<UserResponse> responses = service.getUsers();

            assertThat(responses).isEmpty();

            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Does not expose password hash in responses")
        void getUsers_doesNotExposePasswordHash() {
            User user = UserFixtures.persisted();

            when(userRepository.findAll()).thenReturn(List.of(user));

            List<UserResponse> responses = service.getUsers();

            assertThat(responses).hasSize(1);
            assertThat(responses.getFirst().toString())
                    .doesNotContain(user.getPasswordHash());
        }
    }
}
