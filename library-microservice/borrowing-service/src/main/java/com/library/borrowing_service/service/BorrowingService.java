package com.library.borrowing_service.service;

import com.library.borrowing_service.dto.BookInfo;
import com.library.borrowing_service.dto.BorrowRequest;
import com.library.borrowing_service.dto.BorrowingHistoryResponse;
import com.library.borrowing_service.dto.BorrowingResponse;
import com.library.borrowing_service.dto.UserInfo;
import com.library.borrowing_service.entity.Borrowing;
import com.library.borrowing_service.entity.BorrowingHistory;
import com.library.borrowing_service.repository.BorrowingRepository;
import com.library.borrowing_service.repository.BorrowingHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowingService {

    private static final Logger log = LoggerFactory.getLogger(BorrowingService.class);

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private BorrowingHistoryRepository borrowingHistoryRepository;

    // The default RestTemplate() uses HttpURLConnection under the hood, which
    // hardcodes its list of allowed HTTP methods and does NOT include PATCH -
    // it throws "Invalid HTTP method: PATCH" the instant you try. JdkClientHttpRequestFactory
    // swaps in java.net.http.HttpClient (Java 11+) as the underlying engine instead,
    // which does support PATCH.
    private final RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory());
    private static final String BOOK_SERVICE_URL = "http://localhost:8083/api/books";
    private static final String USER_SERVICE_URL = "http://localhost:8082/api/users";

    private void logHistory(Borrowing borrowing, String status) {
        BorrowingHistory history = new BorrowingHistory();
        history.setBorrowing(borrowing);
        history.setUserId(borrowing.getUserId());
        history.setBookId(borrowing.getBookId());
        history.setStatus(status);
        history.setTimestamp(LocalDateTime.now());
        borrowingHistoryRepository.save(history);
    }

    // Uses execute() with a no-op ResponseExtractor instead of patchForObject(..., Void.class).
    // book-service's /quantity endpoint returns a plain-text status message ("Quantity updated"),
    // but Spring content-negotiates the response Content-Type as application/json based on this
    // client's Accept header - so the body ends up labeled application/json while actually being
    // unquoted plain text. patchForObject(..., Void.class) still tries to Jackson-parse that body
    // and blows up with a StreamReadException ("Unrecognized token 'Quantity'"). We don't care
    // about the response body here at all, so skip conversion entirely.
    private void updateBookQuantity(Long bookId, int delta) {
        restTemplate.execute(
                BOOK_SERVICE_URL + "/" + bookId + "/quantity?delta=" + delta,
                org.springframework.http.HttpMethod.PATCH,
                null,
                response -> null);
    }

    // ---- Enrichment helpers -------------------------------------------------
    // Borrowing/BorrowingHistory only store userId/bookId (by design, since
    // user + book data live in other services). These calls fetch the
    // display fields the frontend needs. Failures are swallowed to null
    // rather than blowing up the whole list response - a missing/deleted
    // user or book shouldn't take down the entire borrowings screen.

    private UserInfo fetchUser(Long userId) {
        if (userId == null) return null;
        try {
            return restTemplate.getForObject(USER_SERVICE_URL + "/" + userId, UserInfo.class);
        } catch (RestClientException e) {
            log.warn("Could not fetch user {} for borrowing enrichment: {}", userId, e.getMessage());
            return null;
        }
    }

    private BookInfo fetchBook(Long bookId) {
        if (bookId == null) return null;
        try {
            return restTemplate.getForObject(BOOK_SERVICE_URL + "/" + bookId, BookInfo.class);
        } catch (RestClientException e) {
            log.warn("Could not fetch book {} for borrowing enrichment: {}", bookId, e.getMessage());
            return null;
        }
    }

    private BorrowingResponse toResponse(Borrowing b) {
        BorrowingResponse r = new BorrowingResponse();
        r.setId(b.getId());
        r.setUser(fetchUser(b.getUserId()));
        r.setBook(fetchBook(b.getBookId()));
        r.setManagerId(b.getManagerId());
        r.setBorrowDate(b.getBorrowDate());
        r.setDueDate(b.getDueDate());
        r.setReturnDate(b.getReturnDate());
        r.setStatus(b.getStatus());
        return r;
    }

    private BorrowingHistoryResponse toHistoryResponse(BorrowingHistory h) {
        BorrowingHistoryResponse r = new BorrowingHistoryResponse();
        r.setId(h.getId());
        r.setUser(fetchUser(h.getUserId()));
        r.setBook(fetchBook(h.getBookId()));
        r.setStatus(h.getStatus());
        r.setTimestamp(h.getTimestamp());
        Borrowing linked = h.getBorrowing();
        if (linked != null) {
            r.setBorrowDate(linked.getBorrowDate());
            r.setDueDate(linked.getDueDate());
            r.setReturnDate(linked.getReturnDate());
        }
        return r;
    }

    // ---- Mutations ------------------------------------------------------------

    public String requestBorrow(BorrowRequest request) {
        Borrowing borrowing = new Borrowing();
        borrowing.setUserId(request.getUserId());
        borrowing.setBookId(request.getBookId());
        borrowing.setBorrowDate(LocalDateTime.now());
        borrowing.setStatus("pending");
        borrowingRepository.save(borrowing);
        logHistory(borrowing, "requested");
        return "Borrow request submitted!";
    }

    public Borrowing approve(Long id, Long managerId) {
        Borrowing borrowing = borrowingRepository.findById(id).orElse(null);
        if (borrowing == null) return null;
        borrowing.setManagerId(managerId);
        borrowing.setStatus("approved");
        borrowing.setDueDate(LocalDateTime.now().plusDays(7));
        borrowingRepository.save(borrowing);
        updateBookQuantity(borrowing.getBookId(), -1);
        logHistory(borrowing, "approved");
        return borrowing;
    }

    public Borrowing reject(Long id) {
        Borrowing borrowing = borrowingRepository.findById(id).orElse(null);
        if (borrowing == null) return null;
        borrowing.setStatus("rejected");
        borrowingRepository.save(borrowing);
        logHistory(borrowing, "rejected");
        return borrowing;
    }

    public Borrowing returnBook(Long id) {
        Borrowing borrowing = borrowingRepository.findById(id).orElse(null);
        if (borrowing == null) return null;
        borrowing.setStatus("returned");
        borrowing.setReturnDate(LocalDateTime.now());
        borrowingRepository.save(borrowing);
        updateBookQuantity(borrowing.getBookId(), 1);
        logHistory(borrowing, "returned");
        return borrowing;
    }

    public Borrowing cancel(Long id) {
        Borrowing borrowing = borrowingRepository.findById(id).orElse(null);
        if (borrowing == null) return null;
        if (!"pending".equals(borrowing.getStatus())) return null;
        borrowing.setStatus("cancelled");
        borrowingRepository.save(borrowing);
        logHistory(borrowing, "cancelled");
        return borrowing;
    }

    // ---- Reads (now enriched) --------------------------------------------------

    public List<BorrowingResponse> getByStatus(String status) {
        return borrowingRepository.findByStatus(status).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<BorrowingResponse> getAllBorrowings() {
        return borrowingRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<BorrowingResponse> getUserBorrowings(Long userId) {
        return borrowingRepository.findByUserId(userId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<BorrowingResponse> getUserInventory(Long userId) {
        return borrowingRepository.findByUserIdAndStatus(userId, "approved").stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<BorrowingHistoryResponse> getUserHistory(Long userId, String status) {
        List<BorrowingHistory> history = (status == null || status.isBlank())
                ? borrowingHistoryRepository.findByUserIdOrderByTimestampDesc(userId)
                : borrowingHistoryRepository.findByUserIdAndStatusOrderByTimestampDesc(userId, status);
        return history.stream().map(this::toHistoryResponse).collect(Collectors.toList());
    }
}