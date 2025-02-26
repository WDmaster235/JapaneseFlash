package com.example.japaneseflash;

import java.util.Dictionary;
import java.util.List;

public class User {
    private String userId;
    private String email;
    private String someDefaultField;
    public Dictionary<String,String> savedCards;

    public User() {
        // Required for Firestore
    }

    public User(String userId, String email, String someDefaultField) {
        this.userId = userId;
        this.email = email;
        this.someDefaultField = someDefaultField;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSomeDefaultField() {
        return someDefaultField;
    }

    public void setSomeDefaultField(String someDefaultField) {
        this.someDefaultField = someDefaultField;
    }
}

