package com.luv2code.io.todos.service;

import com.luv2code.io.todos.config.SecurityConfig;
import com.luv2code.io.todos.entity.Authority;
import com.luv2code.io.todos.entity.User;
import com.luv2code.io.todos.repository.UserRepository;
import com.luv2code.io.todos.request.PasswordUpdatedRequest;
import com.luv2code.io.todos.response.UserResponse;
import com.luv2code.io.todos.util.FindAuthenticatedUser;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final PasswordEncoder passwordEncoder;
    @Override
    @Transactional()
    public UserResponse getUserInfo() {
        User user = findAuthenticatedUser.getAuthenticatedUser();

        return new  UserResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),

                // TODO Fix the empty authority list
                user.getAuthorities().stream().map(auth -> (Authority) auth).toList()
        );
    }

    @Override
    @Transactional
    public void deleteUser() {

        User user = findAuthenticatedUser.getAuthenticatedUser();

        if (isLastAdmin(user)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete yourself, you are the last Admin");
        }
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void updatePassword(PasswordUpdatedRequest passwordUpdatedRequest) {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        if (!isOldPasswordCorrect(passwordUpdatedRequest.getOldPassword(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"Old password is not correct");
        }

        if (!isNewPasswordConfirmed(passwordUpdatedRequest.getNewPassword(), passwordUpdatedRequest.getNewPassword2())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"new password is doesn't match");
        }

        if (!isPasswordDifferent(passwordUpdatedRequest.getNewPassword(), passwordUpdatedRequest.getNewPassword2())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"Choose a different password");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdatedRequest.getNewPassword()));

        userRepository.save(user);



    }

    private boolean isOldPasswordCorrect(String currentPassword, String oldPassword){
        return passwordEncoder.matches(currentPassword, oldPassword);
    }

    private boolean isNewPasswordConfirmed(String newPassword, String newPasswordConfirmation){
        return newPassword.equals(newPasswordConfirmation);
    }

    private boolean isPasswordDifferent(String oldPassword, String newPassword){
        return oldPassword.equals(newPassword);

    }

    private boolean isLastAdmin(User user) {
        boolean userIsAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!userIsAdmin) {
            return false;
        }

        long countAdmin = userRepository.countByAuthority("ROLE_ADMIN");
        return countAdmin <= 1;
    }
}
