package com.bank.cards.dto;

public class AuthResponse {
    
    private String token;
    private String username;
    private String fullName;
    
    // Constructors
    public AuthResponse() {}
    
    public AuthResponse(String token, String username, String fullName) {
        this.token = token;
        this.username = username;
        this.fullName = fullName;
    }
    
    // Getters and setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}