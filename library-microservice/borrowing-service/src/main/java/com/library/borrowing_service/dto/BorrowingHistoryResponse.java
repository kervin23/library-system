package com.library.borrowing_service.dto;

import java.time.LocalDateTime;

public class BorrowingHistoryResponse {
    private Long id;
    private UserInfo user;
    private BookInfo book;
    private String status;
    private LocalDateTime timestamp;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;

    public Long getId() { return id; }
    public UserInfo getUser() { return user; }
    public BookInfo getBook() { return book; }
    public String getStatus() { return status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public LocalDateTime getBorrowDate() { return borrowDate; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getReturnDate() { return returnDate; }

    public void setId(Long id) { this.id = id; }
    public void setUser(UserInfo user) { this.user = user; }
    public void setBook(BookInfo book) { this.book = book; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setBorrowDate(LocalDateTime borrowDate) { this.borrowDate = borrowDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
}
