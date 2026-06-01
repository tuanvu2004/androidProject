package com.example.mobileapp.model;

public class TopicCreateRequest {
    private String name;

    public TopicCreateRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
