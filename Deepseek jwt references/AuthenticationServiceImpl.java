package com.luv2code.io.todos.service;


import com.luv2code.io.todos.entity.Authority;
import com.luv2code.io.todos.entity.User;
import com.luv2code.io.todos.repository.UserRepository;
import com.luv2code.io.todos.request.AuthenticationRequest;
import com.luv2code.io.todos.request.RegisterRequest;
import com.luv2code.io.todos.response.AuthenticationResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
    public void register(RegisterRequest input) throws Exception {

        System.out.println("RRRRRRRRRRR");
        System.out.println("RRRRRRRRRRR");
        System.out.println("RRRRRRRRRRR");
        System.out.println("RRRRRRRRRRR");
        System.out.println("RRRRRRRRRRR");
        System.out.println("RRRRRRRRRRR");
        System.out.println("RRRRRRRRRRR");

        if (isEmailTaken(input.getEmail())){
            throw new Exception("Email is already taken");
        }

        User user = buildNewUser(input);
        userRepository.save(user);
    }

    @Override
    @Transactional()
    public AuthenticationResponse login(AuthenticationRequest input) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
        User user = userRepository.findUserByEmail(input.getEmail()).orElseThrow(() -> new IllegalArgumentException("User is not found"));
        String token = jwtService.generateToken(new HashMap<>(), user);
        return new AuthenticationResponse(token);
    }

    private boolean isEmailTaken(String email){
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

    private List<Authority> initialAuthority(){
        boolean isFirstUser = userRepository.count() == 0;
        List<Authority> authorities = new ArrayList<>();

        authorities.add(new Authority("ROLE_EMPLOYEE"));
        if (isFirstUser){
            authorities.add(new Authority("ROLE_ADMIN"));
        }

        return authorities;
    }





}
