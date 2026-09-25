package com.smartnotes.app.backend.controller;

import com.smartnotes.app.backend.request.UpdateUserRequest;
import com.smartnotes.app.backend.response.UserResponse;
import com.smartnotes.app.backend.rest.AdminController;
import com.smartnotes.app.backend.service.AdminService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTests {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    // ==================== getAllUsers Endpoint ====================

    @Test
    void AdminController_GetAllUsers_UsersListIsReturnedSuccessfully() {
        // Arrange
        List<UserResponse> expectedUsers = new ArrayList<>();

        UserResponse user1 = new UserResponse();
        user1.setId(UUID.randomUUID());
        user1.setFirstName("Alice");
        user1.setEmail("alice@example.com");

        UserResponse user2 = new UserResponse();
        user2.setId(UUID.randomUUID());
        user2.setFirstName("Bob");
        user2.setEmail("bob@example.com");

        expectedUsers.add(user1);
        expectedUsers.add(user2);

        when(adminService.getAllUsers()).thenReturn(expectedUsers);

        // Act
        List<UserResponse> actualUsers = adminController.getAllUsers();

        // Assert
        Assertions.assertThat(actualUsers).isNotNull();
        Assertions.assertThat(actualUsers).hasSize(2);
        Assertions.assertThat(actualUsers.get(0).getEmail()).isEqualTo("alice@example.com");
        Assertions.assertThat(actualUsers.get(1).getEmail()).isEqualTo("bob@example.com");
        verify(adminService).getAllUsers();
    }

    // ==================== updateUser Endpoint ====================

    @Test
    void AdminController_UpdateUser_UserIsUpdatedSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("Alice", "Wonderland", "alice.w@example.com");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(userId);
        expectedResponse.setFirstName("Alice");
        expectedResponse.setLastName("Wonderland");
        expectedResponse.setEmail("alice.w@example.com");

        when(adminService.updateUser(userId, request)).thenReturn(expectedResponse);

        // Act
        UserResponse actualResponse = adminController.updateUser(userId, request);

        // Assert
        Assertions.assertThat(actualResponse).isNotNull();
        Assertions.assertThat(actualResponse.getId()).isEqualTo(userId);
        Assertions.assertThat(actualResponse.getFirstName()).isEqualTo("Alice");
        Assertions.assertThat(actualResponse.getLastName()).isEqualTo("Wonderland");
        Assertions.assertThat(actualResponse.getEmail()).isEqualTo("alice.w@example.com");
        verify(adminService).updateUser(userId, request);
    }

    // ==================== promoteToAdmin Endpoint ====================

    @Test
    void AdminController_PromoteToAdmin_UserIsPromotedSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(userId);
        expectedResponse.setFirstName("Bob");
        expectedResponse.setAuthorities(List.of("ROLE_USER", "ROLE_ADMIN"));

        when(adminService.promoteToAdmin(userId)).thenReturn(expectedResponse);

        // Act
        UserResponse actualResponse = adminController.promoteToAdmin(userId);

        // Assert
        Assertions.assertThat(actualResponse).isNotNull();
        Assertions.assertThat(actualResponse.getId()).isEqualTo(userId);
        Assertions.assertThat(actualResponse.getAuthorities()).contains("ROLE_ADMIN");
        verify(adminService).promoteToAdmin(userId);
    }

    // ==================== deleteUser Endpoint ====================

    @Test
    void AdminController_DeleteUser_UserIsDeletedSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        adminController.deleteUser(userId);

        // Assert
        verify(adminService).deleteUser(userId);
    }
}
