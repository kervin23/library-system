package com.library.book_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "viewed_books")
public class ViewedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private LocalDateTime viewedAt;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Book getBook() { return book; }
    public LocalDateTime getViewedAt() { return viewedAt; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setBook(Book book) { this.book = book; }
    public void setViewedAt(LocalDateTime viewedAt) { this.viewedAt = viewedAt; }
}