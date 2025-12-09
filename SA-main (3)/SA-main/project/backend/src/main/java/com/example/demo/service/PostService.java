package com.example.demo.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.model.Post;

@Service
public class PostService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Random random = new Random();

    public void createPost(Post post) {
        String sql = "INSERT INTO topic_data (title, topic_content, random_name, real_name, topic_time) VALUES (?, ?, ?, ?, NOW())";
        String anonName = "User" + random.nextInt(999999);
        jdbcTemplate.update(sql, post.getTitle(), post.getContent(), anonName, post.getRealName());
    }

    public List<Post> getAllPosts() {
        // Only select non-sensitive info for list
        String sql = "SELECT topic_id, title, topic_content, random_name, topic_time FROM topic_data ORDER BY topic_time DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Post p = new Post();
            p.setId(rs.getInt("topic_id")); // Assuming DB has auto-inc PK 'topic_id'
            p.setTitle(rs.getString("title"));
            p.setContent(rs.getString("topic_content"));
            p.setAuthorAnon(rs.getString("random_name"));
            p.setCreatedAt(rs.getString("topic_time"));
            return p;
        });
    }

    public Post getPostById(int id) {
        String sql = "SELECT topic_id, title, topic_content, random_name, topic_time, real_name FROM topic_data WHERE topic_id = ?";
        List<Post> posts = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Post p = new Post();
            p.setId(rs.getInt("topic_id"));
            p.setTitle(rs.getString("title"));
            p.setContent(rs.getString("topic_content"));
            p.setAuthorAnon(rs.getString("random_name"));
            p.setCreatedAt(rs.getString("topic_time"));
            p.setRealName(rs.getString("real_name")); // Internal use, controller should hide if not admin
            return p;
        }, id);
        return posts.isEmpty() ? null : posts.get(0);
    }

    public String getRealNameByPostId(int id) {
        String sql = "SELECT real_name FROM topic_data WHERE topic_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRealNameByAnon(String anon) {
        String sql = "SELECT real_name FROM topic_data WHERE random_name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, anon);
        } catch (Exception e) {
            return null;
        }
    }
}
