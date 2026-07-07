package com.library.security_service.controller;

import com.library.security_service.dto.LoginRequest;
import com.library.security_service.entity.User;
import com.library.security_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = authService.login(request.getUsername(), request.getPassword());
        if (response == null) return ResponseEntity.status(401).body("Invalid username or password");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        boolean success = authService.register(user);
        if (!success) return ResponseEntity.status(400).body("Username already exists");
        return ResponseEntity.ok("Registration successful");
    }
}