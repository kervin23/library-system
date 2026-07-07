package com.library.book_service.service;

import com.library.book_service.entity.Book;
import com.library.book_service.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() { return bookRepository.findAll(); }

    public Book getById(Long id) { return bookRepository.findById(id).orElse(null); }

    public List<Book> getByCategory(String category) { return bookRepository.findByCategory(category); }

    public void addBook(Book book) {
        book.setStatus("available");
        bookRepository.save(book);
    }

    public void deleteBook(Long id) { bookRepository.deleteById(id); }

        public void updateQuantity(Long id, int delta) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) return;
        book.setQuantity(book.getQuantity() + delta);
        book.setStatus(book.getQuantity() <= 0 ? "unavailable" : "available");
        bookRepository.save(book);
    }
}