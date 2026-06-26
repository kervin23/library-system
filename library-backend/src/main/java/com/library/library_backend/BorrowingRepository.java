package com.library.library_backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    List<Borrowing> findByStatus(String status);
    List<Borrowing> findByUserId(Long userId);
    List<Borrowing> findByUserIdAndStatus(Long userId, String status);
}