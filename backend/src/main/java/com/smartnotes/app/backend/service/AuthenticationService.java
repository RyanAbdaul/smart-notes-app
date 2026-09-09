package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.request.AuthenticationRequest;
import com.smartnotes.app.backend.request.RegisterRequest;
import com.smartnotes.app.backend.response.LoginResponse;

public interface AuthenticationService {
    void register(RegisterRequest input);
    LoginResponse login(AuthenticationRequest input);
}