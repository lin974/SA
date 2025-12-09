package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.PostService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private PostService postService;

    @GetMapping("/realname")
    public ResponseEntity<?> getRealName(@RequestParam String anonCode, @RequestHeader("Authorization") String token) {
        // Verify admin role from token
        if (token == null || !token.contains("admin")) {
            // Simple check: if login was 'admin@gmail.com', token has 'admin' in email
            // part?
            // Login.java had 'admin@gmail.com'.
            // Token format: "fake-token-email".
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
        }

        String name = postService.getRealNameByAnon(anonCode);
        if (name == null) {
            // Try comments? (Logic not fully implemented yet)
            name = "Lookup failed or Comment (Not impl)";
        }

        return ResponseEntity.ok(Map.of("realName", name));
    }
}
