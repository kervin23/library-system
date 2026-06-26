package com.library.library_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

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
        return ResponseEntity.ok("Borrowing approved!");
    }

    // Manager rejects a request
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        Borrowing borrowing = borrowingRepository.findById(id).orElse(null);
        if (borrowing == null) return ResponseEntity.notFound().build();
        borrowing.setStatus("rejected");
        borrowingRepository.save(borrowing);
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
}