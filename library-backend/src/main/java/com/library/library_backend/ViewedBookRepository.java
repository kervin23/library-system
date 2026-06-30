package com.library.library_backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ViewedBookRepository extends JpaRepository<ViewedBook, Long> {
    List<ViewedBook> findByUserIdOrderByViewedAtDesc(Long userId);
}