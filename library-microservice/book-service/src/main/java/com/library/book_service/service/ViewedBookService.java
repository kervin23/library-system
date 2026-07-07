package com.library.book_service.service;

import com.library.book_service.entity.Book;
import com.library.book_service.entity.ViewedBook;
import com.library.book_service.repository.BookRepository;
import com.library.book_service.repository.ViewedBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ViewedBookService {

    @Autowired
    private ViewedBookRepository viewedBookRepository;

    @Autowired
    private BookRepository bookRepository;

    public boolean logView(Long userId, Long bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) return false;

        ViewedBook viewed = new ViewedBook();
        viewed.setUserId(userId);
        viewed.setBook(book);
        viewed.setViewedAt(LocalDateTime.now());
        viewedBookRepository.save(viewed);
        return true;
    }

    public List<ViewedBook> getRecentlyViewed(Long userId, int limit) {
        List<ViewedBook> all = viewedBookRepository.findByUserIdOrderByViewedAtDesc(userId);

        LinkedHashMap<Long, ViewedBook> deduped = new LinkedHashMap<>();
        for (ViewedBook v : all) {
            deduped.putIfAbsent(v.getBook().getId(), v);
        }

        return deduped.values().stream().limit(limit).collect(Collectors.toList());
    }
}