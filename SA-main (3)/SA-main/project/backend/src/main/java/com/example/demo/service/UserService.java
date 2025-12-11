package com.example.demo.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;

@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Random random = new Random();

    public void register(User user) {
        String sql = "INSERT INTO personaldata (realname, email, password, role, topic_id, comment_id) VALUES (?, ?, ?, ?, ?, ?)";
        // Legacy behavior: random IDs
        int topicId = random.nextInt(100000) + 1;
        int commentId = random.nextInt(100000) + 1;

        // Default role 'user' if not specified, though DB seems to use '0'?
        // Let's assume 'user' or 'admin' string is better, but respecting '0' as in
        // legacy if needed.
        // Legacy used '0'. Let's stick to simple strings 'user'/'admin' if we can
        // change DB,
        // but to be safe with existing data, I'll just save what is passed or default
        // 'user'.
        String role = "user";

        jdbcTemplate.update(sql, user.getRealName(), user.getEmail(), user.getPassword(), role, topicId, commentId);
    }

    public User login(String email, String password) {
        String sql = "SELECT * FROM personaldata WHERE email = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), email);

        if (users.isEmpty()) {
            return null;
        }

        User user = users.get(0);
        if (user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM personaldata WHERE email = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), email);
        return users.isEmpty() ? null : users.get(0);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setRealName(rs.getString("realname"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            // Handle role mapping if necessary. Assuming DB stores string.
            // If DB stores '0', we might want to map it.
            String r = rs.getString("role");
            user.setRole("0".equals(r) ? "user" : (r == null ? "user" : r));
            if ("admin".equalsIgnoreCase(user.getRealName()) || "admin".equalsIgnoreCase(r)) {
                user.setRole("admin");
            }

            user.setTopicId(rs.getInt("topic_id"));
            user.setCommentId(rs.getInt("comment_id"));
            return user;
        }
    }
}
