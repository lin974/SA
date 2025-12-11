package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.service.CommentService;
import com.example.demo.service.PostService;

@RestController
@RequestMapping("/api")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private com.example.demo.service.UserService userService;

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody Post post,
            @RequestHeader(value = "Authorization", required = false) String token) {

        String realName = "Anonymous";
        if (token != null && token.startsWith("Bearer fake-token-")) {
            String email = token.replace("Bearer fake-token-", "");
            com.example.demo.model.User user = userService.getUserByEmail(email);
            if (user != null) {
                realName = user.getRealName();
            } else {
                realName = email; // Fallback if user not found but email exists in token
            }
        }
        post.setRealName(realName);

        postService.createPost(post);
        return ResponseEntity.ok(Map.of("message", "Post created"));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPost(@PathVariable int id) {
        Post post = postService.getPostById(id);
        if (post == null)
            return ResponseEntity.notFound().build();

        List<Comment> comments = commentService.getCommentsByTopicId(id);
        post.setComments(comments);

        // Hide realName before returning to non-admins
        post.setRealName(null);
        return ResponseEntity.ok(post);
    }

    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<?> createComment(@PathVariable int id, @RequestBody Comment comment,
            @RequestHeader(value = "Authorization", required = false) String token) {
        comment.setTopicId(id);

        // Extract real user ID from token
        int userId = 0;
        if (token != null && token.startsWith("Bearer fake-token-")) {
            String email = token.replace("Bearer fake-token-", "");
            com.example.demo.model.User user = userService.getUserByEmail(email);
            if (user != null) {
                userId = user.getId();
            }
        }
        comment.setUserId(userId);
        commentService.addComment(comment);
        return ResponseEntity.ok(Map.of("message", "Comment added"));
    }
}
