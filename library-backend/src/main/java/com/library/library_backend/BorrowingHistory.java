package com.library.library_backend;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrowing_history")
public class BorrowingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "borrowing_id")
    private Borrowing borrowing;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    // requested, approved, rejected, returned
    private String status;

    private LocalDateTime timestamp;

    public Long getId() { return id; }
    public Borrowing getBorrowing() { return borrowing; }
    public User getUser() { return user; }
    public Book getBook() { return book; }
    public String getStatus() { return status; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setBorrowing(Borrowing borrowing) { this.borrowing = borrowing; }
    public void setUser(User user) { this.user = user; }
    public void setBook(Book book) { this.book = book; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}