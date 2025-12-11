package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private PostService postService;

    @Autowired
    private com.example.demo.service.CommentService commentService;

    @Autowired
    private UserService userService;

    @GetMapping("/realname")
    public ResponseEntity<?> getRealName(@RequestParam String anonCode, @RequestHeader("Authorization") String token) {
        // Token format: "Bearer fake-token-{email}"
        String email = null;
        if (token != null && token.startsWith("Bearer fake-token-")) {
            email = token.substring("Bearer fake-token-".length());
        }

        if (email == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid token format"));
        }

        User user = userService.getUserByEmail(email);
        if (user == null || !"admin".equals(user.getRole())) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
        }

        String name = postService.getRealNameByAnon(anonCode);
        if (name == null) {
            name = commentService.getRealNameByAnon(anonCode);
        }

        if (name == null) {
            name = "查無此人 (或為舊資料)";
        }

        return ResponseEntity.ok(Map.of("realName", name));
    }
}
