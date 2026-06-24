package com.library.library_backend;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Integer quantity;
    private String status;
    private String category;
    private String coverUrl;
    private String openLibraryId;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public Integer getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public String getCategory() { return category; }
    public String getCoverUrl() { return coverUrl; }
    public String getOpenLibraryId() { return openLibraryId; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setStatus(String status) { this.status = status; }
    public void setCategory(String category) { this.category = category; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public void setOpenLibraryId(String openLibraryId) { this.openLibraryId = openLibraryId; }
}