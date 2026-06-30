package com.library.library_backend;

public class ViewBookRequest {
    private Long userId;
    private Long bookId;

    public Long getUserId() { return userId; }
    public Long getBookId() { return bookId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
}