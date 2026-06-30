package com.library.library_backend;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "viewed_books")
public class ViewedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private LocalDateTime viewedAt;

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Book getBook() { return book; }
    public LocalDateTime getViewedAt() { return viewedAt; }

    public void setUser(User user) { this.user = user; }
    public void setBook(Book book) { this.book = book; }
    public void setViewedAt(LocalDateTime viewedAt) { this.viewedAt = viewedAt; }
}