package com.library.library_backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BorrowingHistoryRepository extends JpaRepository<BorrowingHistory, Long> {
    List<BorrowingHistory> findByUserIdOrderByTimestampDesc(Long userId);
    List<BorrowingHistory> findByUserIdAndStatusOrderByTimestampDesc(Long userId, String status);
}