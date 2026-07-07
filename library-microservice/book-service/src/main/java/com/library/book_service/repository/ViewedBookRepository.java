package com.library.book_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.library.book_service.entity.ViewedBook;

public interface ViewedBookRepository extends JpaRepository<ViewedBook, Long> {
    List<ViewedBook> findByUserIdOrderByViewedAtDesc(Long userId);
}