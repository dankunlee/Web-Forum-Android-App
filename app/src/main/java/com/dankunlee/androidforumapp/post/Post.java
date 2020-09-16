package com.dankunlee.androidforumapp.post;

public class Post {
    int id;
    String title, createdBy, createdDate;
    String content, lastModifiedDate;

    public Post(int id, String title, String createdBy, String createdDate) {
        this.id = id;
        this.title = title;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    public Post(int id, String title, String createdBy, String createdDate, String lastModifiedDate, String content) {
        this.id = id;
        this.title = title;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.content = content;
        this.lastModifiedDate = lastModifiedDate;
    }

    public int getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
