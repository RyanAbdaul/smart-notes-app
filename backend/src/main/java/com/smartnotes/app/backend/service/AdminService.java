package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.Authority;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.AdminDeletionException;
import com.smartnotes.app.backend.exception.EmailAlreadyExistsException;
import com.smartnotes.app.backend.exception.UserNotFoundException;
import com.smartnotes.app.backend.repository.UserRepository;
import com.smartnotes.app.backend.request.UpdateUserRequest;
import com.smartnotes.app.backend.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(this::mapToResponse)
                .toList();
    }

    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName().trim());
        }

        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim();
            if (!newEmail.equalsIgnoreCase(user.getEmail())) {
                userRepository.findUserByEmail(newEmail).ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(user.getId())) {
                        throw new EmailAlreadyExistsException("Email is already taken");
                    }
                });
                user.setEmail(newEmail);
            }
        }

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    public UserResponse promoteToAdmin(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        List<Authority> authorities = user.getAuthorities() != null
                ? user.getAuthorities().stream()
                .map(auth -> new Authority(auth.getAuthority()))
                .collect(Collectors.toCollection(ArrayList::new))
                : new ArrayList<>();

        boolean alreadyAdmin = authorities.stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        if (!alreadyAdmin) {
            authorities.add(new Authority("ROLE_ADMIN"));
            user.setAuthorities(authorities);
            user = userRepository.save(user);
        }

        return mapToResponse(user);
    }

    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        boolean isAdmin = user.getAuthorities() != null && user.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        if (isAdmin) {
            throw new AdminDeletionException("Cannot delete an admin user");
        }

        userRepository.delete(user);
    }

    private UserResponse mapToResponse(User user) {
        List<String> authorities = user.getAuthorities() != null
                ? user.getAuthorities().stream()
                .map(auth -> ((Authority) auth).getAuthority())
                .toList()
                : List.of();

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                authorities,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
