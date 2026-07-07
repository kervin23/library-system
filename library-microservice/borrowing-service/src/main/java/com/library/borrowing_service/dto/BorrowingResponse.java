package com.library.borrowing_service.dto;

import java.time.LocalDateTime;

public class BorrowingResponse {
    private Long id;
    private UserInfo user;
    private BookInfo book;
    private Long managerId;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String status;

    public Long getId() { return id; }
    public UserInfo getUser() { return user; }
    public BookInfo getBook() { return book; }
    public Long getManagerId() { return managerId; }
    public LocalDateTime getBorrowDate() { return borrowDate; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setUser(UserInfo user) { this.user = user; }
    public void setBook(BookInfo book) { this.book = book; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    public void setBorrowDate(LocalDateTime borrowDate) { this.borrowDate = borrowDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
    public void setStatus(String status) { this.status = status; }
}
