package com.smartnotes.app.backend.config;

import com.smartnotes.app.backend.entity.Authority;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevSeedRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String email = "dev@test.com";
        userRepository.findUserByEmail(email).ifPresentOrElse(
            user -> log.info("Dev user already exists: {}", email),
            () -> {
                User dev = new User();
                dev.setFirstName("Dev");
                dev.setLastName("User");
                dev.setEmail(email);
                dev.setPassword(passwordEncoder.encode("devpassword"));
                dev.setAuthorities(List.of(
                        new Authority("ROLE_USER"),
                        new Authority("ROLE_ADMIN")
                ));
                userRepository.save(dev);
                log.info("Created deterministic dev user: {}", email);
            }
        );
    }
}
