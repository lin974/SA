// src/main/java/com/example/controller/ApiController.java
package com.example.controller;

import com.example.entity.Post;
import com.example.entity.Comment;
import com.example.entity.User;
import com.example.entity.PostAnon;
import com.example.entity.CommentAnon;
import com.example.service.ForumService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired private ForumService service;
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('A', 'Z').build();

    record LoginReq(String email, String password) {}
    record RegisterReq(String realName, String email, String password) {}
    record PostReq(String title, String content) {}

    // 登入
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginReq req, HttpServletResponse response) {
        User user = service.login(req.email, req.password);
        String anonCode = generator.generate(5);
        service.saveTodayAnonCode(user.getId(), anonCode);

        String token = Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("role", user.getRole())
                .signWith(key)
                .compact();

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("role", user.getRole());
        map.put("realName", user.getRealName());
        map.put("anonCode", anonCode);
        return map;
    }

    // 註冊（第一個自動 admin）
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req) {
        service.register(req.realName, req.email, req.password);
        return ResponseEntity.ok().build();
    }

    // 所有文章
    @GetMapping("/posts")
    public List<Map<String, Object>> getPosts() {
        return service.getAllPostsWithAnon();
    }

    // 單篇文章
    @GetMapping("/posts/{id}")
    public Map<String, Object> getPost(@PathVariable Long id) {
        return service.getPostDetail(id);
    }

    // 發文
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String auth,
                                        @RequestBody PostReq req) {
        Long userId = getUserIdFromToken(auth);
        String anonCode = generator.generate(5);
        service.saveTodayAnonCode(userId, anonCode);
        service.createPost(userId, req.title, req.content, anonCode);
        return ResponseEntity.status(201).build();
    }

    // 留言
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addComment(@RequestHeader("Authorization") String auth,
                                        @PathVariable Long postId,
                                        @RequestBody Map<String, String> body) {
        Long userId = getUserIdFromToken(auth);
        String anonCode = generator.generate(5);
        service.saveTodayAnonCode(userId, anonCode);
        service.addComment(userId, postId, body.get("content"), anonCode);
        return ResponseEntity.status(201).build();
    }

    // 管理員查真實姓名
    @GetMapping("/admin/realname")
    public Map<String, String> getRealName(@RequestParam String anonCode,
                                           @RequestHeader("Authorization") String auth) {
        Long adminId = getUserIdFromToken(auth);
        if (!"admin".equals(service.getUserById(adminId).getRole())) {
            throw new RuntimeException("無權限");
        }
        String realName = service.getRealNameByAnonCode(anonCode);
        return Map.of("realName", realName);
    }

    private Long getUserIdFromToken(String auth) {
        String token = auth.replace("Bearer ", "");
        return Long.parseLong(Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject());
    }
}
