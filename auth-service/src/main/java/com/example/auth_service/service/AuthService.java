package com.example.auth_service.service;

import com.example.auth_service.dto.LoginRequestDTO;
import com.example.auth_service.module.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
        Optional<User> user = userService.findByEmail(loginRequestDTO.getEmail());
        return null;
    }
}
