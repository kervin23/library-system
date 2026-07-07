package com.library.borrowing_service.dto;

// Lightweight, borrowing-service-local copy of the fields the frontend
// actually needs from book-service's Book.
public class BookInfo {
    private Long id;
    private String title;
    private String author;
    private String coverUrl;

    public BookInfo() {}

    public BookInfo(Long id, String title, String author, String coverUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCoverUrl() { return coverUrl; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}
