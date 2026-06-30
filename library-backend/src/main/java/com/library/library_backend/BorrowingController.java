package com.library.library_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/borrowings")
@CrossOrigin(origins = "http://localhost:5173")
public class BorrowingController {

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowingHistoryRepository borrowingHistoryRepository;

    // helper: log a timestamped history entry every time status changes
    private void logHistory(Borrowing borrowing, String status) {
        BorrowingHistory history = new BorrowingHistory();
        history.setBorrowing(borrowing);
        history.setUser(borrowing.getUser());
        history.setBook(borrowing.getBook());
        history.setStatus(status);
        history.setTimestamp(LocalDateTime.now());
        borrowingHistoryRepository.save(history);
    }

    // User requests to borrow a book
    @PostMapping("/request")
    public ResponseEntity<?> requestBorrow(@RequestBody BorrowRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        Book book = bookRepository.findById(request.getBookId()).orElse(null);

        if (user == null || book == null)
            return ResponseEntity.badRequest().body("User or book not found");

        if (book.getQuantity() <= 0)
            return ResponseEntity.badRequest().body("Book not available");

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setBook(book);
        borrowing.setBorrowDate(LocalDateTime.now());
        borrowing.setStatus("pending");

        borrowingRepository.save(borrowing);
        logHistory(borrowing, "requested");
        return ResponseEntity.ok("Borrow request submitted!");
    }

    // Manager gets all pending requests
    @GetMapping("/pending")
    public List<Borrowing> getPending() {
        return borrowingRepository.findByStatus("pending");
    }

    // Manager approves a request
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id, @RequestParam Long managerId) {
        Borrowing borrowing = borrowingRepository.findById(id).orElse(null);
        if (borrowing == null) return ResponseEntity.notFound().build();

        User manager = userRepository.findById(managerId).orElse(null);
        borrowing.setManager(manager);
        borrowing.setStatus("approved");
        borrowing.setDueDate(LocalDateTime.now().plusDays(7));

        // reduce book quantity
        Book book = borrowing.getBook();
        book.setQuantity(book.getQuantity() - 1);
        if (book.getQuantity() == 0) book.setStatus("unavailable");
        bookRepository.save(book);

        borrowingRepository.save(borrowing);
        logHistory(borrowing, "approved");
        return ResponseEntity.ok("Borrowing approved!");
    }

    // Manager rejects a request
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        Borrowing borrowing = borrowingRepository.findById(id).orElse(null);
        if (borrowing == null) return ResponseEntity.notFound().build();
        borrowing.setStatus("rejected");
        borrowingRepository.save(borrowing);
        logHistory(borrowing, "rejected");
        return ResponseEntity.ok("Borrowing rejected!");
    }

    // Manager marks as returned
    @PutMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
        Borrowing borrowing = borrowingRepository.findById(id).orElse(null);
        if (borrowing == null) return ResponseEntity.notFound().build();

        borrowing.setStatus("returned");
        borrowing.setReturnDate(LocalDateTime.now());

        // increase book quantity back
        Book book = borrowing.getBook();
        book.setQuantity(book.getQuantity() + 1);
        if (book.getQuantity() > 0) book.setStatus("available");
        bookRepository.save(book);

        borrowingRepository.save(borrowing);
        logHistory(borrowing, "returned");
        return ResponseEntity.ok("Book returned!");
    }

    // Get all active borrowings
    @GetMapping("/active")
    public List<Borrowing> getActive() {
        return borrowingRepository.findByStatus("approved");
    }

    // Get user's borrowings
    @GetMapping("/user/{userId}")
    public List<Borrowing> getUserBorrowings(@PathVariable Long userId) {
        return borrowingRepository.findByUserId(userId);
    }

    // Get user's current inventory (books currently approved, not yet returned)
    @GetMapping("/inventory/{userId}")
    public List<Borrowing> getUserInventory(@PathVariable Long userId) {
        return borrowingRepository.findByUserIdAndStatus(userId, "approved");
    }

    // Get user's full timestamped history, optionally filtered by status
    // e.g. GET /api/borrowings/history/3  or  GET /api/borrowings/history/3?status=rejected
    @GetMapping("/history/{userId}")
    public List<BorrowingHistory> getUserHistory(
            @PathVariable Long userId,
            @RequestParam(required = false) String status) {
        if (status == null || status.isBlank()) {
            return borrowingHistoryRepository.findByUserIdOrderByTimestampDesc(userId);
        }
        return borrowingHistoryRepository.findByUserIdAndStatusOrderByTimestampDesc(userId, status);
    }

// Cancel a pending borrow request (user-initiated, before manager acts on it)
@PutMapping("/cancel/{id}")
public ResponseEntity<?> cancelBorrowing(@PathVariable Long id) {
    Borrowing borrowing = borrowingRepository.findById(id).orElse(null);
    if (borrowing == null) return ResponseEntity.notFound().build();

    if (!"pending".equals(borrowing.getStatus())) {
        return ResponseEntity.badRequest().body("Only pending requests can be cancelled");
    }

    borrowing.setStatus("cancelled");
    borrowingRepository.save(borrowing);
    logHistory(borrowing, "cancelled");

    return ResponseEntity.ok("Borrow request cancelled!");
}

// Manager's return history — all books marked returned
@GetMapping("/returned")
public List<Borrowing> getReturned() {
    return borrowingRepository.findByStatus("returned");
}


// Admin: get every borrowing record across all users
@GetMapping
public List<Borrowing> getAllBorrowings() {
    return borrowingRepository.findAll();
}
}