package com.library.library_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:5173")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/category/{category}")
    public List<Book> getByCategory(@PathVariable String category) {
        return bookRepository.findByCategory(category);
    }

    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        book.setStatus("available");
        bookRepository.save(book);
        return ResponseEntity.ok("Book added successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return ResponseEntity.ok("Book deleted");
    }
}