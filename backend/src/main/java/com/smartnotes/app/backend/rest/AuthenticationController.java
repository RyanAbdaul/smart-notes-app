package com.smartnotes.app.backend.rest;

import com.smartnotes.app.backend.request.AuthenticationRequest;
import com.smartnotes.app.backend.request.RegisterRequest;
import com.smartnotes.app.backend.response.LoginResponse;
import com.smartnotes.app.backend.service.AuthenticationService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration API")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();

    private Bucket newBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(5).refillGreedy(5, Duration.ofMinutes(1)))
                .build();
    }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with email, password, and personal information. First user gets ADMIN role, subsequent users get USER role."
    )

    public void register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) throws Exception {
        String ip = httpRequest.getRemoteAddr();
        Bucket bucket = registerBuckets.computeIfAbsent(ip, k -> newBucket());
        if (!bucket.tryConsume(1)) {
            throw new Exception("Too many registration attempts, try again later");
        }

        authenticationService.register(request);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticates a user with email and password, returns a JWT token for subsequent API requests"
    )
    public LoginResponse login(@Valid @RequestBody AuthenticationRequest request) {
        System.out.println("TEST");
        return authenticationService.login(request);
    }
}