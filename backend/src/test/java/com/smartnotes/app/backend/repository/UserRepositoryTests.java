package com.smartnotes.app.backend.repository;

import com.smartnotes.app.backend.entity.Authority;
import com.smartnotes.app.backend.entity.Role;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.request.RegisterRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
@Import(UserRepositoryTests.PasswordEncoderTestConfig.class)
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @TestConfiguration
    static class PasswordEncoderTestConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    private RegisterRequest buildRegisterRequest(String firstName, String lastName, String email, String password) {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    @Test
    void UserRepository_CreateUser_UserIsSavedSuccessfully() {
        // Arrange
        RegisterRequest request = buildRegisterRequest("test1", "test2", "test@gmail.com", "password");

        // Act
        User user = buildNewUser(request);
        User savedUser = userRepository.save(user);

        // Assert
        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals("test@gmail.com", savedUser.getEmail());
        Assertions.assertTrue(passwordEncoder.matches("password", savedUser.getPassword()));
    }

    @Test
    void UserRepository_FindUserById_UserIsFetchedByIdSuccessfully() {
        // Arrange
        RegisterRequest request = buildRegisterRequest("John", "Doe", "john.doe@example.com", "password");
        User user = buildNewUser(request);
        User savedUser = userRepository.save(user);

        // Act
        Optional<User> result = userRepository.findById(savedUser.getId());

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(savedUser.getId(), result.get().getId());
        Assertions.assertEquals("john.doe@example.com", result.get().getEmail());
    }

    @Test
    void UserRepository_FindUserById_UserNotFound_ReturnsEmpty() {
        // Act
        Optional<User> result = userRepository.findById(UUID.randomUUID());

        // Assert
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    void UserRepository_FindAll_UsersAreFetchedSuccessfully() {
        // Arrange
        RegisterRequest request1 = buildRegisterRequest("test1", "test2", "test1@gmail.com", "password");
        RegisterRequest request2 = buildRegisterRequest("test2", "test2", "test2@gmail.com", "password");
        
        userRepository.save(buildNewUser(request1));
        userRepository.save(buildNewUser(request2));

        // Act
        List<User> users = (List<User>) userRepository.findAll();

        // Assert
        Assertions.assertFalse(users.isEmpty());
        Assertions.assertEquals(2, users.size());
    }

    @Test
    void UserRepository_FindAll_NoUsers_ReturnsEmptyList() {
        // Act
        List<User> users = (List<User>) userRepository.findAll();

        // Assert
        Assertions.assertTrue(users.isEmpty());
    }

    @Test
    void UserRepository_FindUserByEmail_UserExists_ReturnsUser() {
        // Arrange
        RegisterRequest request = buildRegisterRequest("John", "Doe", "john.doe@example.com", "password");
        userRepository.save(buildNewUser(request));

        // Act
        Optional<User> result = userRepository.findUserByEmail("john.doe@example.com");

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("john.doe@example.com", result.get().getEmail());
    }

    @Test
    void UserRepository_FindUserByEmail_UserNotFound_ReturnsEmpty() {
        // Act
        Optional<User> result = userRepository.findUserByEmail("nonexistent@example.com");

        // Assert
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    void UserRepository_CountByAuthority_RoleExists_ReturnsCount() {
        // Arrange
        // First user will have both ROLE_USER and ROLE_ADMIN
        RegisterRequest request1 = buildRegisterRequest("Admin", "User", "admin@example.com", "password");
        userRepository.save(buildNewUser(request1));

        // Second user will have only ROLE_USER
        RegisterRequest request2 = buildRegisterRequest("Regular", "User", "user@example.com", "password");
        userRepository.save(buildNewUser(request2));

        // Act
        long userCount = userRepository.countByAuthority(Role.ROLE_USER.name());
        long adminCount = userRepository.countByAuthority(Role.ROLE_ADMIN.name());

        // Assert
        Assertions.assertEquals(2, userCount); // Both have ROLE_USER
        Assertions.assertEquals(1, adminCount); // Only the first one has ROLE_ADMIN
    }

    @Test
    void UserRepository_CountByAuthority_RoleDoesNotExist_ReturnsZero() {
        // Act - don't save any user, count nonexistent role
        long count = userRepository.countByAuthority("ROLE_NONEXISTENT");

        // Assert
        Assertions.assertEquals(0, count);
    }

    @Test
    void UserRepository_DeleteById_UserIsDeletedSuccessfully() {
        // Arrange
        RegisterRequest request = buildRegisterRequest("John", "Doe", "john.doe@example.com", "password");
        User savedUser = userRepository.save(buildNewUser(request));
        UUID userId = savedUser.getId();

        // Act
        userRepository.deleteById(userId);
        Optional<User> deletedUser = userRepository.findById(userId);

        // Assert
        Assertions.assertFalse(deletedUser.isPresent());
    }

    @Test
    void UserRepository_ExistsById_UserExists_ReturnsTrue() {
        // Arrange
        RegisterRequest request = buildRegisterRequest("John", "Doe", "john.doe@example.com", "password");
        User savedUser = userRepository.save(buildNewUser(request));

        // Act
        boolean exists = userRepository.existsById(savedUser.getId());

        // Assert
        Assertions.assertTrue(exists);
    }

    @Test
    void UserRepository_ExistsById_UserDoesNotExist_ReturnsFalse() {
        // Act
        boolean exists = userRepository.existsById(UUID.randomUUID());

        // Assert
        Assertions.assertFalse(exists);
    }

    private User buildNewUser(RegisterRequest input) {
        User user = new User();
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setAuthorities(initialAuthority());
        return user;
    }

    private List<Authority> initialAuthority() {
        boolean isFirstUser = userRepository.count() == 0;
        List<Authority> authorities = new ArrayList<>();

        authorities.add(new Authority(Role.ROLE_USER.name()));
        if (isFirstUser) {
            authorities.add(new Authority(Role.ROLE_ADMIN.name()));
        }

        return authorities;
    }
}
