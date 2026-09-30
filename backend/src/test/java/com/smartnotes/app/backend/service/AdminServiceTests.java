package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.Authority;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.AdminDeletionException;
import com.smartnotes.app.backend.exception.EmailAlreadyExistsException;
import com.smartnotes.app.backend.exception.UserNotFoundException;
import com.smartnotes.app.backend.repository.UserRepository;
import com.smartnotes.app.backend.request.UpdateUserRequest;
import com.smartnotes.app.backend.response.UserResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setFirstName("Alice");
        testUser.setLastName("Smith");
        testUser.setEmail("alice@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setAuthorities(new ArrayList<>(List.of(new Authority("ROLE_USER"))));
    }

    // ==================== getAllUsers ====================

    @Test
    void AdminService_GetAllUsers_UsersAreReturnedSuccessfully() {
        // Arrange
        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setFirstName("Bob");
        user2.setLastName("Jones");
        user2.setEmail("bob@example.com");
        user2.setAuthorities(List.of(new Authority("ROLE_USER")));

        when(userRepository.findAll()).thenReturn(List.of(testUser, user2));

        // Act
        List<UserResponse> responses = adminService.getAllUsers();

        // Assert
        Assertions.assertThat(responses).hasSize(2);
        Assertions.assertThat(responses.get(0).getEmail()).isEqualTo("alice@example.com");
        Assertions.assertThat(responses.get(1).getEmail()).isEqualTo("bob@example.com");
        Mockito.verify(userRepository).findAll();
    }

    @Test
    void AdminService_GetAllUsers_EmptyList_ReturnsEmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());

        // Act
        List<UserResponse> responses = adminService.getAllUsers();

        // Assert
        Assertions.assertThat(responses).isEmpty();
        Mockito.verify(userRepository).findAll();
    }

    // ==================== updateUser ====================

    @Test
    void AdminService_UpdateUser_UserIsUpdatedSuccessfully() {
        // Arrange
        UpdateUserRequest request = new UpdateUserRequest("Alicia", "Johnson", "alicia@example.com");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByEmail("alicia@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse response = adminService.updateUser(testUserId, request);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getFirstName()).isEqualTo("Alicia");
        Assertions.assertThat(response.getLastName()).isEqualTo("Johnson");
        Assertions.assertThat(response.getEmail()).isEqualTo("alicia@example.com");
        Mockito.verify(userRepository).findById(testUserId);
        Mockito.verify(userRepository).findUserByEmail("alicia@example.com");
        Mockito.verify(userRepository).save(testUser);
    }

    @Test
    void AdminService_UpdateUser_SameEmail_UserIsUpdatedSuccessfully() {
        // Arrange
        UpdateUserRequest request = new UpdateUserRequest("Alicia", "Smith", "alice@example.com");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse response = adminService.updateUser(testUserId, request);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getFirstName()).isEqualTo("Alicia");
        Assertions.assertThat(response.getEmail()).isEqualTo("alice@example.com");
        Mockito.verify(userRepository).findById(testUserId);
        Mockito.verify(userRepository, Mockito.never()).findUserByEmail(Mockito.any());
        Mockito.verify(userRepository).save(testUser);
    }

    @Test
    void AdminService_UpdateUser_UserNotFound_ThrowsUserNotFoundException() {
        // Arrange
        UUID unknownId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("NewName", "NewLast", "new@example.com");

        when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> adminService.updateUser(unknownId, request))
                .isInstanceOf(UserNotFoundException.class);
        Mockito.verify(userRepository).findById(unknownId);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void AdminService_UpdateUser_EmailAlreadyExists_ThrowsEmailAlreadyExistsException() {
        // Arrange
        UpdateUserRequest request = new UpdateUserRequest("Alicia", "Johnson", "existing@example.com");

        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setEmail("existing@example.com");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByEmail("existing@example.com")).thenReturn(Optional.of(otherUser));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> adminService.updateUser(testUserId, request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email is already taken");
        Mockito.verify(userRepository).findById(testUserId);
        Mockito.verify(userRepository).findUserByEmail("existing@example.com");
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    // ==================== promoteToAdmin ====================

    @Test
    void AdminService_PromoteToAdmin_UserIsPromotedSuccessfully() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse response = adminService.promoteToAdmin(testUserId);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getAuthorities()).contains("ROLE_ADMIN", "ROLE_USER");
        Mockito.verify(userRepository).findById(testUserId);
        Mockito.verify(userRepository).save(testUser);
    }

    @Test
    void AdminService_PromoteToAdmin_UserAlreadyAdmin_ReturnsUserWithoutDuplicateRole() {
        // Arrange
        testUser.setAuthorities(new ArrayList<>(List.of(new Authority("ROLE_USER"), new Authority("ROLE_ADMIN"))));
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // Act
        UserResponse response = adminService.promoteToAdmin(testUserId);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getAuthorities()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        Mockito.verify(userRepository).findById(testUserId);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void AdminService_PromoteToAdmin_UserNotFound_ThrowsUserNotFoundException() {
        // Arrange
        UUID unknownId = UUID.randomUUID();
        when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> adminService.promoteToAdmin(unknownId))
                .isInstanceOf(UserNotFoundException.class);
        Mockito.verify(userRepository).findById(unknownId);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    // ==================== deleteUser ====================

    @Test
    void AdminService_DeleteUser_UserIsDeletedSuccessfully() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // Act
        adminService.deleteUser(testUserId);

        // Assert
        Mockito.verify(userRepository).findById(testUserId);
        Mockito.verify(userRepository).delete(testUser);
    }

    @Test
    void AdminService_DeleteUser_UserNotFound_ThrowsUserNotFoundException() {
        // Arrange
        UUID unknownId = UUID.randomUUID();
        when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> adminService.deleteUser(unknownId))
                .isInstanceOf(UserNotFoundException.class);
        Mockito.verify(userRepository).findById(unknownId);
        Mockito.verify(userRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void AdminService_DeleteUser_UserIsAdmin_ThrowsAdminDeletionException() {
        // Arrange
        testUser.setAuthorities(List.of(new Authority("ROLE_USER"), new Authority("ROLE_ADMIN")));
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> adminService.deleteUser(testUserId))
                .isInstanceOf(AdminDeletionException.class)
                .hasMessageContaining("Cannot delete an admin user");
        Mockito.verify(userRepository).findById(testUserId);
        Mockito.verify(userRepository, Mockito.never()).delete(Mockito.any());
    }
}
