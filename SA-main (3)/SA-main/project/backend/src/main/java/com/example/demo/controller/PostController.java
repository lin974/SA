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

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody Post post,
            @RequestHeader(value = "Authorization", required = false) String token) {
        // In real app, extract user from token. For demo, we might need 'realName' in
        // body or assume admin/test.
        // Frontend doesn't send realName in body, only title/content.
        // We need to look up realName from token.
        // Hack: Since token is "fake-token-EMAIL", we can parse email and query User.
        // To be simple: I'll require realName in body momentarily or default.
        // Wait, Frontend main.html: 'body: JSON.stringify({ title, content })'.
        // It sends Authorization header.
        // I will default realName to "Unknown" or parse from token if possible.
        // I'll parse the email from token.

        String realName = "Anonymous";
        if (token != null && token.startsWith("Bearer fake-token-")) {
            realName = token.replace("Bearer fake-token-", "");
            // In a real DB lookup we'd get the name from this email.
            // For now, let's just use the email as realName or 'User'
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
        // comment.setUserId(...) from token
        // For now, using random user id or 0
        comment.setUserId(0);
        commentService.addComment(comment);
        return ResponseEntity.ok(Map.of("message", "Comment added"));
    }
}
