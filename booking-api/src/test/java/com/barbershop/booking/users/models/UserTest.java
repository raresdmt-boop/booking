package com.barbershop.booking.users.models;

import com.barbershop.booking.support.UserFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("User - unit tests")
public class UserTest
{

    @Test
    @DisplayName("Constructor testing")
    void UserTest()
    {
        User user = new User(
                "test@gmail.com",
                "hashedPassword123",
                Role.CUSTOMER
        );

        assertThat(user.getId()).isNull();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getEmail()).isEqualTo("test@gmail.com");
        assertThat(user.getPasswordHash()).isEqualTo("hashedPassword123");
        assertThat(user.getRole()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    @DisplayName("Same user instance should be equal")
    void equals_sameInstance_returnsTrue() {

        User user = new User(
                "test@gmail.com",
                "hashedPassword123",
                Role.CUSTOMER
        );

        boolean result = user.equals(user);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Different users with null IDs should not be equal")
    void equals_differentUsersWithNullIds_returnsFalse() {

        User user1 = new User("a@gmail.com", "pass1", Role.CUSTOMER);
        User user2 = new User("a@gmail.com", "pass1", Role.CUSTOMER);

        boolean result = user1.equals(user2);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Users with the same ID should be equal")
    void equals_sameId_returnsTrue() {

        User user1 = UserFixtures.persisted();
        User user2 = UserFixtures.persisted();

        boolean result = user1.equals(user2);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Users with different IDs should not be equal")
    void equals_differentIds_returnsFalse() {

        User user1 = UserFixtures.persisted();
        User user2 = UserFixtures.withId(UserFixtures.OTHER_ID);

        boolean result = user1.equals(user2);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("User compared with null should not be equal")
    void equals_null_returnsFalse() {

        User user = UserFixtures.persisted();

        boolean result = user.equals(null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("User compared with another object type should not be equal")
    void equals_differentObjectType_returnsFalse() {

        User user = UserFixtures.persisted();
        String otherObject = "test";

        boolean result = user.equals(otherObject);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("User with ID compared to user without ID should not be equal")
    void equals_oneNullId_returnsFalse() {

        User user1 = UserFixtures.persisted();
        User user2 = UserFixtures.user();

        boolean result = user1.equals(user2);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Users with the same ID should have the same hashCode")
    void hashCode_sameId_returnsSameHashCode() {

        User user1 = UserFixtures.persisted();
        User user2 = UserFixtures.persisted();

        int hash1 = user1.hashCode();
        int hash2 = user2.hashCode();

        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("Users with different IDs should have the same hashCode")
    void hashCode_differentIds_returnsSameHashCode() {

        User user1 = UserFixtures.persisted();
        User user2 = UserFixtures.withId(UserFixtures.OTHER_ID);

        int hash1 = user1.hashCode();
        int hash2 = user2.hashCode();

        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("toString should contain user information")
    void toString_returnsUserInformation() {

        User user = UserFixtures.persisted();

        String result = user.toString();

        assertThat(result)
                .contains("id=1")
                .contains("email='test@gmail.com'")
                .contains("role=CUSTOMER")
                .doesNotContain("hashedPassword123");
    }
}
