package com.library.borrowing_service.controller;

import com.library.borrowing_service.dto.BorrowRequest;
import com.library.borrowing_service.dto.BorrowingHistoryResponse;
import com.library.borrowing_service.dto.BorrowingResponse;
import com.library.borrowing_service.entity.Borrowing;
import com.library.borrowing_service.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {

    @Autowired
    private BorrowingService borrowingService;

    @PostMapping("/request")
    public ResponseEntity<?> requestBorrow(@RequestBody BorrowRequest request) {
        return ResponseEntity.ok(borrowingService.requestBorrow(request));
    }

    @GetMapping("/pending")
    public List<BorrowingResponse> getPending() { return borrowingService.getByStatus("pending"); }

    @GetMapping("/active")
    public List<BorrowingResponse> getActive() { return borrowingService.getByStatus("approved"); }

    @GetMapping("/returned")
    public List<BorrowingResponse> getReturned() { return borrowingService.getByStatus("returned"); }

    @GetMapping
    public List<BorrowingResponse> getAllBorrowings() { return borrowingService.getAllBorrowings(); }

    @GetMapping("/user/{userId}")
    public List<BorrowingResponse> getUserBorrowings(@PathVariable Long userId) {
        return borrowingService.getUserBorrowings(userId);
    }

    @GetMapping("/inventory/{userId}")
    public List<BorrowingResponse> getUserInventory(@PathVariable Long userId) {
        return borrowingService.getUserInventory(userId);
    }

    @GetMapping("/history/{userId}")
    public List<BorrowingHistoryResponse> getUserHistory(
            @PathVariable Long userId,
            @RequestParam(required = false) String status) {
        return borrowingService.getUserHistory(userId, status);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id, @RequestParam Long managerId) {
        Borrowing b = borrowingService.approve(id, managerId);
        if (b == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Borrowing approved!");
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        Borrowing b = borrowingService.reject(id);
        if (b == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Borrowing rejected!");
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
        Borrowing b = borrowingService.returnBook(id);
        if (b == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Book returned!");
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        Borrowing b = borrowingService.cancel(id);
        if (b == null) return ResponseEntity.badRequest().body("Only pending requests can be cancelled");
        return ResponseEntity.ok("Borrow request cancelled!");
    }
}
