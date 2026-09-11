package com.smartnotes.app.backend.rest;

import com.smartnotes.app.backend.entity.Authority;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.repository.UserRepository;
import com.smartnotes.app.backend.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dev")
@Profile("dev")
@AllArgsConstructor
@Tag(name = "Development", description = "Development-only utilities")
public class DevController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/token")
    @Operation(
            summary = "Generate dev JWT",
            description = "Returns a valid JWT for the dev test user (dev@test.com). Only available in 'dev' profile. Valid for 7 days."
    )
    public DevTokenResponse getDevToken() {
        String email = "dev@test.com";
        User user = userRepository.findUserByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setFirstName("Dev");
            newUser.setLastName("User");
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode("devpassword"));
            newUser.setAuthorities(List.of(new Authority("ROLE_USER"), new Authority("ROLE_ADMIN")));
            return userRepository.save(newUser);
        });

        Map<String, Object> claims = new HashMap<>();
        // 7 days in milliseconds
        long sevenDays = 7 * 24 * 60 * 60 * 1000L;
        String token = jwtService.generateToken(claims, user, sevenDays);

        return new DevTokenResponse(token);
    }

    @Data
    @AllArgsConstructor
    static class DevTokenResponse {
        private String token;
    }
}
