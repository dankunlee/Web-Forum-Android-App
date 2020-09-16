package com.dankunlee.androidforumapp.comment;

public class Comment {
    int id;
    String createdBy, createdDate, content;

    public Comment(int id, String content, String createdBy, String createdDate) {
        this.id = id;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
