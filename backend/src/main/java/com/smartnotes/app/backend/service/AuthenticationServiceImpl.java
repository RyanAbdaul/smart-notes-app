package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.Authority;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.EmailAlreadyExistsException;
import com.smartnotes.app.backend.exception.UserNotFoundException;
import com.smartnotes.app.backend.repository.UserRepository;
import com.smartnotes.app.backend.request.AuthenticationRequest;
import com.smartnotes.app.backend.request.RegisterRequest;
import com.smartnotes.app.backend.response.LoginResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void register(RegisterRequest input) {
        if (isEmailTaken(input.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already taken");
        }

        User user = buildNewUser(input);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public LoginResponse login(AuthenticationRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
        User user = userRepository.findUserByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        String token = jwtService.generateToken(new HashMap<>(), user);
        
        // Get user's primary role (first authority)
        // String role = user.getAuthorities().isEmpty() ? "ROLE_USER" 
        //         : user.getAuthorities().get(0).getAuthority();
        
        return new LoginResponse(token);
    }

    private boolean isEmailTaken(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    private User buildNewUser(RegisterRequest input) {
        User user = new User();
        user.setId(0);
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setAuthorities(initialAuthority());
        return user;
    }

    private List<Authority> initialAuthority() {
        boolean isFirstUser = userRepository.count() == 0;
        List<Authority> authorities = new ArrayList<>();

        authorities.add(new Authority("ROLE_USER"));
        if (isFirstUser) {
            authorities.add(new Authority("ROLE_ADMIN"));
        }

        return authorities;
    }
}
