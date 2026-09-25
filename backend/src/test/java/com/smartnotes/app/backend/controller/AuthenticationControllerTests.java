package com.smartnotes.app.backend.controller;

import com.smartnotes.app.backend.request.AuthenticationRequest;
import com.smartnotes.app.backend.request.RegisterRequest;
import com.smartnotes.app.backend.response.LoginResponse;
import com.smartnotes.app.backend.rest.AuthenticationController;
import com.smartnotes.app.backend.service.AuthenticationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTests {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    // ==================== register Endpoint ====================

    @Test
    void AuthenticationController_Register_UserIsRegisteredSuccessfully() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");

        // Act
        authenticationController.register(request);

        // Assert
        verify(authenticationService).register(request);
    }

    // ==================== login Endpoint ====================

    @Test
    void AuthenticationController_Login_UserIsLoggedInSuccessfully() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("john.doe@example.com", "password123");

        LoginResponse expectedResponse = new LoginResponse("sample-jwt-token", "ROLE_USER");

        when(authenticationService.login(request)).thenReturn(expectedResponse);

        // Act
        LoginResponse actualResponse = authenticationController.login(request);

        // Assert
        Assertions.assertThat(actualResponse).isNotNull();
        Assertions.assertThat(actualResponse.getToken()).isEqualTo("sample-jwt-token");
        Assertions.assertThat(actualResponse.getRole()).isEqualTo("ROLE_USER");
        verify(authenticationService).login(request);
    }
}
