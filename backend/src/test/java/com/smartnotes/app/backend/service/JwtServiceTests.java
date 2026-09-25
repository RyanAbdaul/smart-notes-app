package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

class JwtServiceTests {

    private JwtServiceImpl jwtService;
    private User testUser;
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "JWT_EXPIRATION", EXPIRATION);

        testUser = new User();
        testUser.setEmail("john.doe@example.com");
    }

    // ==================== generateToken ====================

    @Test
    void JwtService_GenerateToken_DefaultExpiration_GeneratesValidToken() {
        // Act
        String token = jwtService.generateToken(new HashMap<>(), testUser);

        // Assert
        Assertions.assertThat(token).isNotBlank();
        String extractedUsername = jwtService.extractUsername(token);
        Assertions.assertThat(extractedUsername).isEqualTo("john.doe@example.com");
    }

    @Test
    void JwtService_GenerateToken_CustomExpiration_GeneratesValidToken() {
        // Act
        String token = jwtService.generateToken(new HashMap<>(), testUser, 10000L);

        // Assert
        Assertions.assertThat(token).isNotBlank();
        String extractedUsername = jwtService.extractUsername(token);
        Assertions.assertThat(extractedUsername).isEqualTo("john.doe@example.com");
    }

    @Test
    void JwtService_GenerateToken_WithExtraClaims_GeneratesTokenSuccessfully() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ROLE_ADMIN");
        extraClaims.put("customField", "customValue");

        // Act
        String token = jwtService.generateToken(extraClaims, testUser);

        // Assert
        Assertions.assertThat(token).isNotBlank();
        String extractedUsername = jwtService.extractUsername(token);
        Assertions.assertThat(extractedUsername).isEqualTo("john.doe@example.com");
    }

    // ==================== extractUsername ====================

    @Test
    void JwtService_ExtractUsername_ValidToken_ReturnsUsername() {
        // Arrange
        String token = jwtService.generateToken(new HashMap<>(), testUser);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        Assertions.assertThat(username).isEqualTo("john.doe@example.com");
    }

    @Test
    void JwtService_ExtractUsername_InvalidSignature_ThrowsSignatureException() {
        // Arrange
        // Create token with a different key
        JwtServiceImpl anotherJwtService = new JwtServiceImpl();
        String differentSecretKey = "6158726548576D5A7134743777217A25432A462D4A614E645267556B58703273";
        ReflectionTestUtils.setField(anotherJwtService, "SECRET_KEY", differentSecretKey);
        ReflectionTestUtils.setField(anotherJwtService, "JWT_EXPIRATION", EXPIRATION);

        String foreignToken = anotherJwtService.generateToken(new HashMap<>(), testUser);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> jwtService.extractUsername(foreignToken))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    void JwtService_ExtractUsername_MalformedToken_ThrowsJwtException() {
        // Arrange
        String malformedToken = "invalid.token.string";

        // Act & Assert
        Assertions.assertThatThrownBy(() -> jwtService.extractUsername(malformedToken))
                .isInstanceOf(JwtException.class);
    }

    // ==================== isTokenValid ====================

    @Test
    void JwtService_IsTokenValid_ValidTokenAndMatchingUser_ReturnsTrue() {
        // Arrange
        String token = jwtService.generateToken(new HashMap<>(), testUser);

        // Act
        boolean isValid = jwtService.isTokenValid(token, testUser);

        // Assert
        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void JwtService_IsTokenValid_DifferentUser_ReturnsFalse() {
        // Arrange
        String token = jwtService.generateToken(new HashMap<>(), testUser);

        User anotherUser = new User();
        anotherUser.setEmail("different.user@example.com");

        // Act
        boolean isValid = jwtService.isTokenValid(token, anotherUser);

        // Assert
        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    void JwtService_IsTokenValid_ExpiredToken_ThrowsExpiredJwtException() {
        // Arrange
        // Expiration in the past (-1000ms)
        String expiredToken = jwtService.generateToken(new HashMap<>(), testUser, -1000L);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> jwtService.isTokenValid(expiredToken, testUser))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
