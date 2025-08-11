package com.yourcompany.phonestore.model;

import jakarta.persistence.*; // Đã cập nhật từ javax sang jakarta

@Entity // Đánh dấu đây là một thực thể
@Table(name = "Users") // Ánh xạ tới bảng 'Users' trong CSDL
public class User {

    @Id // Đánh dấu đây là khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID được tự động sinh ra bởi CSDL
    @Column(name = "id") // Ánh xạ tới cột 'id'
    private int id;

    @Column(name = "username", unique = true, nullable = false) // Cột 'username', không được null và là duy nhất
    private String username;

    @Column(name = "password", nullable = false) // Cột 'password', không được null
    private String password;

    @Column(name = "email", unique = true, nullable = false) // Cột 'email', không được null và là duy nhất
    private String email;

    @Column(name = "role", nullable = false) // Cột 'role', không được null
    private String role;

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
