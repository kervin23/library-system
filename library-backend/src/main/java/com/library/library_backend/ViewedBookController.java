package com.library.library_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/viewed-books")
@CrossOrigin(origins = "http://localhost:5173")
public class ViewedBookController {

    @Autowired
    private ViewedBookRepository viewedBookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    // Log that a user viewed a book
    @PostMapping
    public ResponseEntity<?> logView(@RequestBody ViewBookRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        Book book = bookRepository.findById(request.getBookId()).orElse(null);

        if (user == null || book == null)
            return ResponseEntity.badRequest().body("User or book not found");

        ViewedBook viewed = new ViewedBook();
        viewed.setUser(user);
        viewed.setBook(book);
        viewed.setViewedAt(LocalDateTime.now());
        viewedBookRepository.save(viewed);

        return ResponseEntity.ok("View logged");
    }

    // Get a user's recently viewed books, deduplicated (most recent view per book), newest first
    @GetMapping("/{userId}")
    public List<ViewedBook> getRecentlyViewed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {

        List<ViewedBook> all = viewedBookRepository.findByUserIdOrderByViewedAtDesc(userId);

        // keep only the most recent view per distinct book
        java.util.LinkedHashMap<Long, ViewedBook> deduped = new java.util.LinkedHashMap<>();
        for (ViewedBook v : all) {
            deduped.putIfAbsent(v.getBook().getId(), v);
        }

        return deduped.values().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}