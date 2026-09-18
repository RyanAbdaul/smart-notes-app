package com.smartnotes.app.backend.repository;

import com.smartnotes.app.backend.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void UserRepository_CreateUser_UserIsSavedSuccessfully() {

        // Arrange
        User user = new User();
        user.setFirstName("test1");
        user.setLastName("test2");
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals("test@gmail.com", savedUser.getEmail());

    }
    @Test
    void UserRepository_FindUserById_UserIsFetchedByIdSuccessfully() {

        // Arrange
        User user = new User();
        user.setFirstName("test1");
        user.setLastName("test2");
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");


        User savedUser = userRepository.save(user);

        // Act
        Optional<User> result = userRepository.findById(savedUser.getId());

        // Assert
        Assertions.assertNotNull(result.get());
    }
    @Test
    void UserRepository_FindAll_UsersAreFetchedSuccessfully() {

        // Arrange
        User user1 = new User();
        user1.setFirstName("test1");
        user1.setLastName("test2");
        user1.setEmail("test@gmail.com");
        user1.setPassword("encodedPassword");

        User user2 = new User();
        user2.setFirstName("test1");
        user2.setLastName("test2");
        user2.setEmail("test2@gmail.com");
        user2.setPassword("encodedPassword");

        // Act
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        List<User> users = (List<User>) userRepository.findAll();

        // Assert
        Assertions.assertFalse(users.isEmpty());
        Assertions.assertEquals(2, users.size());
    }
}