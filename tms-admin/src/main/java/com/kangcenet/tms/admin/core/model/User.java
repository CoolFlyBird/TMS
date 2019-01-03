package com.kangcenet.tms.admin.core.model;

public class User {
    private String username;
    private String password;
    private String email;
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean checkAdmin() {
        return "ADMIN".equals(role);
    }

    @Override
    public String toString() {
        return "User{ username:" + username + "\n" +
                "password:" + password + "\n" +
                "email:" + email + "\n" +
                "role:" + role + "\n" +
                "}";
    }
}
