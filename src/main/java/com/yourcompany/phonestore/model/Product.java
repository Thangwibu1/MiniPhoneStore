package com.yourcompany.phonestore.model;

import jakarta.persistence.*; // import jakarta.persistence để sử dụng JPA
@Entity
@Table(name = "Products") // Ánh xạ tới bảng 'Products' trong CSDL
public class Product {
    @Id // Khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID tự tăng
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false) // Tên sản phẩm, không được null
    private String name;

    @Column(name = "description") // Mô tả sản phẩm
    private String description;

    @Column(name = "price", nullable = false) // Giá, không được null
    private double price;

    @Column(name = "stock", nullable = false) // Tồn kho, không được null
    private int stock;

    @Column(name = "imageUrl") // Đường dẫn ảnh
    private String imageUrl;
    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
