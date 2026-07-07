package com.library.borrowing_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.library.borrowing_service.entity.BorrowingHistory;

public interface BorrowingHistoryRepository extends JpaRepository<BorrowingHistory, Long> {
    List<BorrowingHistory> findByUserIdOrderByTimestampDesc(Long userId);
    List<BorrowingHistory> findByUserIdAndStatusOrderByTimestampDesc(Long userId, String status);
}