package com.library.book_service.controller;

import com.library.book_service.dto.ViewBookRequest;
import com.library.book_service.entity.ViewedBook;
import com.library.book_service.service.ViewedBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/viewed-books")
public class ViewedBookController {

    @Autowired
    private ViewedBookService viewedBookService;

    @PostMapping
    public ResponseEntity<?> logView(@RequestBody ViewBookRequest request) {
        boolean success = viewedBookService.logView(request.getUserId(), request.getBookId());
        if (!success) return ResponseEntity.badRequest().body("Book not found");
        return ResponseEntity.ok("View logged");
    }

    @GetMapping("/{userId}")
    public List<ViewedBook> getRecentlyViewed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return viewedBookService.getRecentlyViewed(userId, limit);
    }
}