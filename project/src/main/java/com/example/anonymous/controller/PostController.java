package com.example.anonymous.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.anonymous.model.Comment;
import com.example.anonymous.model.Post;
import com.example.anonymous.model.User;
import com.example.anonymous.repository.CommentRepository;
import com.example.anonymous.repository.PostRepository;
import com.example.anonymous.repository.UserRepository;
import com.example.anonymous.util.Anonymize;

import lombok.Data;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private Long getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return null;
        String token = authHeader.substring(7);
        return AuthController.tokenStore.get(token);
    }

    @GetMapping
    public List<PostDTO> listPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(p -> {
            PostDTO dto = new PostDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            dto.setAnonCode(p.getRandomName());
            dto.setCreatedAt(p.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            return dto;
        }).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> body) {
        Long userId = getUserIdFromToken(auth);
        if (userId == null)
            return ResponseEntity.status(401).body("Unauthorized");

        User user = userRepository.findById(userId).orElseThrow();

        Post post = new Post();
        post.setTitle(body.get("title"));
        post.setContent(body.get("content"));
        post.setRealName(user.getRealName());
        post.setRandomName(Anonymize.newAnon());

        postRepository.save(post);
        return ResponseEntity.ok(Map.of("message", "Post created"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null)
            return ResponseEntity.notFound().build();

        PostDetailDTO dto = new PostDetailDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAnonCode(post.getRandomName());
        dto.setCreatedAt(post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(id);
        dto.setComments(comments.stream().map(c -> {
            CommentDTO cd = new CommentDTO();
            cd.setId(c.getId());
            cd.setAnonCode(c.getAuthorAnon());
            cd.setContent(c.getContent());
            cd.setCreatedAt(c.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            return cd;
        }).collect(Collectors.toList()));

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long id, @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> body) {
        Long userId = getUserIdFromToken(auth);
        if (userId == null)
            return ResponseEntity.status(401).body("Unauthorized");

        Post post = postRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUserId(userId);
        comment.setAuthorAnon(Anonymize.newAnon());
        comment.setContent(body.get("content"));

        commentRepository.save(comment);
        return ResponseEntity.ok(Map.of("message", "Comment added"));
    }

    @Data
    static class PostDTO {
        private Long id;
        private String title;
        private String anonCode;
        private String createdAt;
    }

    @Data
    @lombok.EqualsAndHashCode(callSuper = true)
    static class PostDetailDTO extends PostDTO {
        private String content;
        private List<CommentDTO> comments;
    }

    @Data
    static class CommentDTO {
        private Long id;
        private String anonCode;
        private String content;
        private String createdAt;
    }
}
