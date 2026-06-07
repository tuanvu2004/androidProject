package com.example.mobileapp.model;

public class TopicCreateRequest {
    private String name;
    private String userEmail;

    public TopicCreateRequest(String name, String userEmail) {
        this.name = name;
        this.userEmail = userEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
