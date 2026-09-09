package com.luv2code.io.todos.service;

import com.luv2code.io.todos.request.AuthenticationRequest;
import com.luv2code.io.todos.request.RegisterRequest;
import com.luv2code.io.todos.response.AuthenticationResponse;

public interface AuthenticationService {
    void register(RegisterRequest input) throws Exception;
    AuthenticationResponse login(AuthenticationRequest input) throws Exception;
}
