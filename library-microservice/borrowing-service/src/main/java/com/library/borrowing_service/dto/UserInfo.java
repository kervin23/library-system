package com.library.borrowing_service.dto;

// Lightweight, borrowing-service-local copy of the fields the frontend
// actually needs from security-service's User. Deliberately excludes
// password/email - we only fetch what's displayed.
public class UserInfo {
    private Long id;
    private String username;
    private String role;

    public UserInfo() {}

    public UserInfo(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setRole(String role) { this.role = role; }
}
