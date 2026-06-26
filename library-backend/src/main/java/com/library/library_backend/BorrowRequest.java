package com.library.library_backend;

public class BorrowRequest {
    private Long userId;
    private Long bookId;

    public Long getUserId() { return userId; }
    public Long getBookId() { return bookId; }
}