package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User user = userService.login(email, password);
        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("token", "fake-token-" + email); // JWT in real app
            response.put("role", user.getRole());
            response.put("realName", user.getRealName());
            response.put("anonCode", "ANON-" + (int) (Math.random() * 1000)); // Session based anon?
            // Frontend uses anonCode from login for display? Or maybe just for
            // currentUser.anon?
            // "currentUser = { role: data.role, anon: data.anonCode };"
            // So we return a per-session random anon code for *this* user?
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body(Map.of("message", "Login failed"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            userService.register(user);
            return ResponseEntity.ok(Map.of("message", "Registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }
}
