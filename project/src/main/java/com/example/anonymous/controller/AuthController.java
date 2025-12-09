package com.example.anonymous.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.anonymous.model.User;
import com.example.anonymous.repository.UserRepository;
import com.example.anonymous.util.Anonymize;

import lombok.Data;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Simulate a token store (In production, use JWT or Redis)
    public static final Map<String, Long> tokenStore = new HashMap<>();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User user = userRepository.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) { // In real app, hash password!
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("anonCode", Anonymize.newAnon()); // Generate a temporary anon code for session
        response.put("role", user.getRole() == null ? "user" : user.getRole());
        response.put("realName", user.getRealName());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
        }

        User user = new User();
        user.setRealName(req.getRealName());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        user.setRole("user"); // Default role

        // Admin hardcode for demo purposes if email is admin@gmail.com
        if ("admin@gmail.com".equals(req.getEmail())) {
            user.setRole("admin");
        }

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Registered successfully"));
    }

    @Data
    static class RegisterRequest {
        private String realName;
        private String email;
        private String password;
    }
}
