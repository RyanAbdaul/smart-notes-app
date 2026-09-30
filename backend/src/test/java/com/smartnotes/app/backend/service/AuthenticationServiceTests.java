package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.Authority;
import com.smartnotes.app.backend.entity.Role;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.EmailAlreadyExistsException;
import com.smartnotes.app.backend.exception.UserNotFoundException;
import com.smartnotes.app.backend.repository.UserRepository;
import com.smartnotes.app.backend.request.AuthenticationRequest;
import com.smartnotes.app.backend.request.RegisterRequest;
import com.smartnotes.app.backend.response.LoginResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("plainPassword");

        authRequest = new AuthenticationRequest("john.doe@example.com", "plainPassword");
    }

    // ==================== register ====================

    @Test
    void AuthenticationService_Register_FirstUser_SavedWithUserAndAdminRoles() {
        // Arrange
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        // Act
        authenticationService.register(registerRequest);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        Assertions.assertThat(savedUser.getFirstName()).isEqualTo("John");
        Assertions.assertThat(savedUser.getLastName()).isEqualTo("Doe");
        Assertions.assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");
        Assertions.assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        Assertions.assertThat(savedUser.getAuthorities())
                .extracting(auth -> ((Authority) auth).getAuthority())
                .containsExactlyInAnyOrder(Role.ROLE_USER.name(), Role.ROLE_ADMIN.name());
    }

    @Test
    void AuthenticationService_Register_SubsequentUser_SavedWithUserRoleOnly() {
        // Arrange
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(1L);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        // Act
        authenticationService.register(registerRequest);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        Assertions.assertThat(savedUser.getAuthorities())
                .extracting(auth -> ((Authority) auth).getAuthority())
                .containsExactly(Role.ROLE_USER.name());
    }

    @Test
    void AuthenticationService_Register_EmailAlreadyExists_ThrowsEmailAlreadyExistsException() {
        // Arrange
        User existingUser = new User();
        existingUser.setEmail("john.doe@example.com");
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> authenticationService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email is already taken");
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    // ==================== login ====================

    @Test
    void AuthenticationService_Login_RegularUser_ReturnsTokenAndUserRole() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("john.doe@example.com");
        user.setAuthorities(List.of(new Authority(Role.ROLE_USER.name())));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(), Mockito.eq(user))).thenReturn("mock-jwt-token");

        // Act
        LoginResponse response = authenticationService.login(authRequest);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getToken()).isEqualTo("mock-jwt-token");
        Assertions.assertThat(response.getRole()).isEqualTo(Role.ROLE_USER.name());
        Mockito.verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        Mockito.verify(userRepository).findUserByEmail("john.doe@example.com");
    }

    @Test
    void AuthenticationService_Login_AdminUser_ReturnsTokenAndAdminRole() {
        // Arrange
        User adminUser = new User();
        adminUser.setId(UUID.randomUUID());
        adminUser.setEmail("john.doe@example.com");
        adminUser.setAuthorities(List.of(new Authority(Role.ROLE_USER.name()), new Authority(Role.ROLE_ADMIN.name())));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(Optional.of(adminUser));
        when(jwtService.generateToken(any(), Mockito.eq(adminUser))).thenReturn("mock-admin-jwt-token");

        // Act
        LoginResponse response = authenticationService.login(authRequest);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getToken()).isEqualTo("mock-admin-jwt-token");
        Assertions.assertThat(response.getRole()).isEqualTo(Role.ROLE_ADMIN.name());
        Mockito.verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void AuthenticationService_Login_InvalidCredentials_ThrowsBadCredentialsException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> authenticationService.login(authRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid credentials");
        Mockito.verify(userRepository, Mockito.never()).findUserByEmail(any());
        Mockito.verify(jwtService, Mockito.never()).generateToken(any(), any());
    }

    @Test
    void AuthenticationService_Login_UserNotFound_ThrowsUserNotFoundException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> authenticationService.login(authRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
        Mockito.verify(jwtService, Mockito.never()).generateToken(any(), any());
    }
}
