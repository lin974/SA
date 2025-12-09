package com.example.demo.model;

import java.util.List;

public class Post {
    private int id; // topic_id
    private String title;
    private String content; // topic_content
    private String authorAnon; // random_name
    private String realName; // real_name
    private String createdAt; // topic_time
    private List<Comment> comments;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorAnon() {
        return authorAnon;
    }

    public void setAuthorAnon(String authorAnon) {
        this.authorAnon = authorAnon;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
