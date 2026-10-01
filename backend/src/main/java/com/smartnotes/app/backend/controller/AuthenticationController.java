package com.smartnotes.app.backend.controller;

import com.smartnotes.app.backend.annotation.RateLimit;
import com.smartnotes.app.backend.annotation.RateLimitType;
import com.smartnotes.app.backend.exception.TooManyAttemptsException;
import com.smartnotes.app.backend.request.AuthenticationRequest;
import com.smartnotes.app.backend.request.RegisterRequest;
import com.smartnotes.app.backend.response.LoginResponse;
import com.smartnotes.app.backend.service.AuthenticationService;
import com.smartnotes.app.backend.service.RateLimiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration API")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RateLimiterService rateLimiterService;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with email, password, and personal information. First user gets ADMIN role, subsequent users get USER role."
    )
    @RateLimit(endpoint = "register", type = RateLimitType.STRICT)
    public void register(@Valid @RequestBody RegisterRequest request) throws Exception {
        authenticationService.register(request);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticates a user with email and password, returns a JWT token for subsequent API requests"
    )
    @RateLimit(endpoint = "login", type = RateLimitType.STRICT)
    public LoginResponse login(@Valid @RequestBody AuthenticationRequest request) {
        System.out.println("TEST");
        return authenticationService.login(request);
    }
}