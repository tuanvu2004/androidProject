package com.example.mobileapp.model;

public class RegisterRequest {
    private int id;
    private boolean isDeleted;
    private String email;
    private String password;
    private boolean enabled;

    public RegisterRequest(String email, String password) {
        this.id = 0;
        this.isDeleted = false;
        this.email = email;
        this.password = password;
        this.enabled = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
