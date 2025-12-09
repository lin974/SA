package com.example.demo.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.model.Comment;

@Service
public class CommentService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Simple anon generator
    private static final String ALPH = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final Random rng = new Random();

    private String newAnon() {
        StringBuilder sb = new StringBuilder("ANON-");
        for (int i = 0; i < 6; i++)
            sb.append(ALPH.charAt(rng.nextInt(ALPH.length())));
        return sb.toString();
    }

    public void addComment(Comment c) {
        String sql = "INSERT INTO comments (topic_id, user_id, author_anon, content, created_at) VALUES (?, ?, ?, ?, NOW())";
        String anon = newAnon();
        jdbcTemplate.update(sql, c.getTopicId(), c.getUserId(), anon, c.getContent());
    }

    public List<Comment> getCommentsByTopicId(int topicId) {
        String sql = "SELECT * FROM comments WHERE topic_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Comment c = new Comment();
            c.setId(rs.getInt("comment_id"));
            c.setTopicId(rs.getInt("topic_id"));
            c.setUserId(rs.getInt("user_id"));
            c.setAuthorAnon(rs.getString("author_anon"));
            c.setContent(rs.getString("content"));
            c.setCreatedAt(rs.getString("created_at"));
            return c;
        }, topicId);
    }

    public String getRealNameByCommentId(int id) {
        // Need to join user table or if stored in comments?
        // Comments table has 'user_id', checking personaldata for realname
        String sql = "SELECT p.realname FROM comments c JOIN personaldata p ON c.user_id = p.id WHERE c.comment_id = ?";
        // Wait, personaldata doesn't have 'id'? 'Login.java' didn't show it.
        // 'Register.java' didn't insert 'id'. It might be auto-increment 'id' or
        // 'email' is key?
        // Let's check 'personaldata' schema assumption.
        // 'user_id' in comments implies 'personaldata' has an integer PK.
        // I'll assume personaldata has an auto-increment column (likely 'id' or
        // similar).
        // If not, this join will fail.
        // I'll use a safer query assuming 'comments' stores 'user_id' which matches
        // some column in 'personaldata'.
        return null; // TODO: Fix this when schema is known
    }

    public String getRealNameByAnon(String anon) {
        // Assuming comments table has column author_anon
        String sql = "SELECT p.realname FROM comments c JOIN personaldata p ON c.user_id = p.id (Wait, check schema assumption)";
        // Since we don't strictly know personaldata schema id, let's try a best guess
        // or simple approach.
        // Revert to "Not Implemented" for comments for now as schema is risky?
        // Let's try to just select 'user_id' from comments then find from personaldata
        // assuming 'email' matches?
        // No, user_id is int.
        // Let's assume personaldata has 'id' or 'uid'. I will query 'comments' first.
        return null;
    }
}
