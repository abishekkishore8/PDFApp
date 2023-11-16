package com.example.javaapp;

public class User {
    private String name;
    private String email;
    private String pdfLocation;

    // Constructor
    public User(String name, String email, String pdfLocation) {
        this.name = name;
        this.email = email;
        this.pdfLocation = pdfLocation;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPdfLocation() {
        return pdfLocation;
    }
}
