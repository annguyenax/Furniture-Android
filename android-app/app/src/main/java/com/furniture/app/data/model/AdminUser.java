package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdminUser {

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("status")
    private String status;

    @SerializedName("roles")
    private List<String> roles;

    @SerializedName("createdAt")
    private String createdAt;

    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public List<String> getRoles() { return roles; }
    public String getCreatedAt() { return createdAt; }

    public String getDisplayName() {
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";
        String full = (first + " " + last).trim();
        return !full.isEmpty() ? full : (username != null ? username : "");
    }

    public boolean isActive() { return "active".equalsIgnoreCase(status); }
    public boolean isBanned() { return "banned".equalsIgnoreCase(status); }
}
