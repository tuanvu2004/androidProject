package com.example.mobileapp.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "local_topics")
public class LocalTopic {
    @PrimaryKey
    private Long id;
    private String name;
    private String userEmail;
    private int totalWords;
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public int getTotalWords() { return totalWords; }
    public void setTotalWords(int totalWords) { this.totalWords = totalWords; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
