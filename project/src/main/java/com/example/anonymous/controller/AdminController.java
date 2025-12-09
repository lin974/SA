package com.example.anonymous.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.anonymous.model.Comment;
import com.example.anonymous.model.Post;
import com.example.anonymous.model.User;
import com.example.anonymous.repository.CommentRepository;
import com.example.anonymous.repository.PostRepository;
import com.example.anonymous.repository.UserRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository; // Need to fetch user from comment if needed?
    // Wait, comment table only stores userId, so we can find user. But comment
    // table also has authorAnon.
    // The request is ?anonCode=...
    // Need to find if anonCode belongs to a Post or a Comment.

    private Long getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return null;
        String token = authHeader.substring(7);
        return AuthController.tokenStore.get(token);
    }

    @GetMapping("/realname")
    public ResponseEntity<?> getRealName(@RequestHeader("Authorization") String auth, @RequestParam String anonCode) {
        Long userId = getUserIdFromToken(auth);
        if (userId == null)
            return ResponseEntity.status(401).body("Unauthorized");

        User admin = userRepository.findById(userId).orElse(null);
        if (admin == null || !"admin".equals(admin.getRole())) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        // Try to find in Posts
        Post post = postRepository.findByRandomName(anonCode);
        if (post != null) {
            return ResponseEntity.ok(Map.of("realName", post.getRealName()));
        }

        // Try to find in Comments
        Comment comment = commentRepository.findByAuthorAnon(anonCode);
        if (comment != null) {
            User user = userRepository.findById(comment.getUserId()).orElse(null);
            if (user != null) {
                return ResponseEntity.ok(Map.of("realName", user.getRealName()));
            }
        }

        return ResponseEntity.notFound().build();
    }
}
