
package com.barbershop.booking.users.services;

import com.barbershop.booking.support.UserFixtures;
import com.barbershop.booking.users.dtos.ChangePasswordRequest;
import com.barbershop.booking.users.dtos.UserCreateRequest;
import com.barbershop.booking.users.dtos.UserResponse;
import com.barbershop.booking.users.dtos.UserUpdateRequest;
import com.barbershop.booking.users.exceptions.UserEmailAlreadyExists;
import com.barbershop.booking.users.exceptions.UserExceptionConstants;
import com.barbershop.booking.users.exceptions.UserNotFound;
import com.barbershop.booking.users.models.Role;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandServiceImpl unit tests")
class UserCommandServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserCommandServiceImpl service;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Nested
    @DisplayName("addUser")
    class AddUser {

        private final UserCreateRequest request =
                new UserCreateRequest(
                        "test@gmail.com",
                        "password123",
                        Role.CUSTOMER
                );

        @Test
        @DisplayName("Saves user and returns persisted data")
        void addUser_success() {
            when(userRepository.save(any(User.class)))
                    .thenReturn(UserFixtures.persisted());

            UserResponse response = service.addUser(request);

            assertThat(response.id()).isEqualTo(UserFixtures.KNOWN_ID);
            assertThat(response.email()).isEqualTo("test@gmail.com");
            assertThat(response.role()).isEqualTo(Role.CUSTOMER);

            verify(userRepository).existsByEmail("test@gmail.com");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Builds entity from request fields")
        void addUser_buildsEntityCorrectly() {
            when(userRepository.save(any(User.class)))
                    .thenReturn(UserFixtures.persisted());

            service.addUser(request);

            verify(userRepository).save(userCaptor.capture());

            User saved = userCaptor.getValue();

            assertThat(saved.getEmail()).isEqualTo("test@gmail.com");
            assertThat(saved.getPasswordHash()).isEqualTo("password123");
            assertThat(saved.getRole()).isEqualTo(Role.CUSTOMER);
        }

        @Test
        @DisplayName("Throws exception when email already exists")
        void addUser_emailAlreadyExists() {
            when(userRepository.existsByEmail("test@gmail.com"))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.addUser(request))
                    .isInstanceOf(UserEmailAlreadyExists.class)
                    .hasMessage(UserExceptionConstants.USER_EMAIL_ALREADY_EXISTS);

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

        @Test
        @DisplayName("Throws exception when user does not exist")
        void updateUser_userNotFound() {
            when(userRepository.findById(UserFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            UserUpdateRequest request =
                    new UserUpdateRequest("new@gmail.com", Role.BARBER);

            assertThatThrownBy(() ->
                    service.updateUser(UserFixtures.OTHER_ID, request))
                    .isInstanceOf(UserNotFound.class)
                    .hasMessage(UserExceptionConstants.USER_ID_NOT_FOUND);
        }

        @Test
        @DisplayName("Updates email and role")
        void updateUser_success() {
            User existing = UserFixtures.persisted();

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(existing));

            UserUpdateRequest request =
                    new UserUpdateRequest("new@gmail.com", Role.BARBER);

            UserResponse response =
                    service.updateUser(UserFixtures.KNOWN_ID, request);

            assertThat(existing.getEmail()).isEqualTo("new@gmail.com");
            assertThat(existing.getRole()).isEqualTo(Role.BARBER);
            assertThat(response.email()).isEqualTo("new@gmail.com");
            assertThat(response.role()).isEqualTo(Role.BARBER);

            verify(userRepository).existsByEmail("new@gmail.com");
        }

        @Test
        @DisplayName("Null fields leave existing values unchanged")
        void updateUser_nullFields() {
            User existing = UserFixtures.persisted();

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(existing));

            UserUpdateRequest request =
                    new UserUpdateRequest(null, null);

            UserResponse response =
                    service.updateUser(UserFixtures.KNOWN_ID, request);

            assertThat(response.email()).isEqualTo("test@gmail.com");
            assertThat(response.role()).isEqualTo(Role.CUSTOMER);

            verify(userRepository, never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("Blank email is ignored")
        void updateUser_blankEmail() {
            User existing = UserFixtures.persisted();

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(existing));

            service.updateUser(
                    UserFixtures.KNOWN_ID,
                    new UserUpdateRequest("   ", null)
            );

            assertThat(existing.getEmail()).isEqualTo("test@gmail.com");

            verify(userRepository, never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("Same email skips uniqueness check")
        void updateUser_sameEmail() {
            User existing = UserFixtures.persisted();

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(existing));

            service.updateUser(
                    UserFixtures.KNOWN_ID,
                    new UserUpdateRequest("test@gmail.com", null)
            );

            assertThat(existing.getEmail()).isEqualTo("test@gmail.com");

            verify(userRepository, never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("Throws exception when new email is already used")
        void updateUser_emailAlreadyExists() {
            User existing = UserFixtures.persisted();

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(existing));

            when(userRepository.existsByEmail("taken@gmail.com"))
                    .thenReturn(true);

            UserUpdateRequest request =
                    new UserUpdateRequest("taken@gmail.com", null);

            assertThatThrownBy(() ->
                    service.updateUser(UserFixtures.KNOWN_ID, request))
                    .isInstanceOf(UserEmailAlreadyExists.class);

            assertThat(existing.getEmail()).isEqualTo("test@gmail.com");
        }

        @Test
        @DisplayName("Update does not call save")
        void updateUser_doesNotCallSave() {
            User existing = UserFixtures.persisted();

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(existing));

            service.updateUser(
                    UserFixtures.KNOWN_ID,
                    new UserUpdateRequest(null, Role.BARBER)
            );

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteUser")
    class DeleteUser {

        @Test
        @DisplayName("Deletes existing user and returns response")
        void deleteUser_success() {
            User existing = UserFixtures.persisted();

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(existing));

            UserResponse response =
                    service.deleteUser(UserFixtures.KNOWN_ID);

            verify(userRepository).delete(existing);

            assertThat(response.id()).isEqualTo(UserFixtures.KNOWN_ID);
            assertThat(response.email()).isEqualTo("test@gmail.com");
            assertThat(response.role()).isEqualTo(Role.CUSTOMER);
        }

        @Test
        @DisplayName("Throws exception when user does not exist")
        void deleteUser_userNotFound() {
            when(userRepository.findById(UserFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    service.deleteUser(UserFixtures.OTHER_ID))
                    .isInstanceOf(UserNotFound.class);

            verify(userRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePassword {

        @Test
        @DisplayName("Changes password for existing user")
        void changePassword_success() {
            User existing = UserFixtures.persisted();

            when(userRepository.findById(UserFixtures.KNOWN_ID))
                    .thenReturn(Optional.of(existing));

            ChangePasswordRequest request =
                    new ChangePasswordRequest("newPassword123");

            UserResponse response =
                    service.changePassword(UserFixtures.KNOWN_ID, request);

            assertThat(existing.getPasswordHash())
                    .isEqualTo("newPassword123");

            assertThat(response.id()).isEqualTo(UserFixtures.KNOWN_ID);
            assertThat(response.toString())
                    .doesNotContain("newPassword123");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Throws exception when user does not exist")
        void changePassword_userNotFound() {
            when(userRepository.findById(UserFixtures.OTHER_ID))
                    .thenReturn(Optional.empty());

            ChangePasswordRequest request =
                    new ChangePasswordRequest("newPassword123");

            assertThatThrownBy(() ->
                    service.changePassword(UserFixtures.OTHER_ID, request))
                    .isInstanceOf(UserNotFound.class);
        }
    }
}
