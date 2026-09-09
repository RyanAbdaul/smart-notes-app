package com.luv2code.io.todos.service;

import com.luv2code.io.todos.entity.User;
import com.luv2code.io.todos.request.PasswordUpdatedRequest;
import com.luv2code.io.todos.response.UserResponse;
import org.springframework.stereotype.Service;

public interface UserService {
    UserResponse getUserInfo();
    void deleteUser();
    void updatePassword(PasswordUpdatedRequest passwordUpdatedRequest);
}
