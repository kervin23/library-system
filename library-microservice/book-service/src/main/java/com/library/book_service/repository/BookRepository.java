package com.library.book_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.library.book_service.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategory(String category);
    List<Book> findByStatus(String status);
}