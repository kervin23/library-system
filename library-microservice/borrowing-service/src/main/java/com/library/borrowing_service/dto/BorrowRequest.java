package com.library.borrowing_service.dto;

public class BorrowRequest {
    private Long userId;
    private Long bookId;

    public Long getUserId() { return userId; }
    public Long getBookId() { return bookId; }
}