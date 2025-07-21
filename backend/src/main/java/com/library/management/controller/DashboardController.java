package com.library.management.controller;

import com.library.management.entity.Book;
import com.library.management.entity.BorrowRecord;
import com.library.management.service.BookService;
import com.library.management.service.BorrowService;
import com.library.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BorrowService borrowService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Basic counts
        stats.put("totalBooks", bookService.getTotalBookCount());
        stats.put("totalUsers", userService.getTotalUserCount());
        stats.put("activeBorrows", borrowService.getActiveBorrowCount());
        stats.put("overdueBooks", borrowService.getOverdueCount());
        
        // Popular books (top 5 most reviewed)
        List<Book> popularBooks = bookService.findMostReviewedBooks(PageRequest.of(0, 5)).getContent();
        stats.put("popularBooks", popularBooks);
        
        // Recent activity (last 10 borrow records)
        List<BorrowRecord> recentActivity = borrowService.getRecentActivity(PageRequest.of(0, 10)).getContent();
        stats.put("recentActivity", recentActivity);
        
        // Due soon (next 7 days)
        List<BorrowRecord> dueSoon = borrowService.getRecordsDueSoon(7);
        stats.put("dueSoon", dueSoon);
        
        // Overdue records
        List<BorrowRecord> overdue = borrowService.getOverdueRecords();
        stats.put("overdueRecords", overdue);
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/user-stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Available books count
        stats.put("availableBooks", bookService.getAvailableBookCount());
        
        // Top rated books (top 5)
        List<Book> topRated = bookService.findTopRatedBooks(PageRequest.of(0, 5)).getContent();
        stats.put("topRatedBooks", topRated);
        
        // Recently added books (top 10)
        List<Book> recentBooks = bookService.findAllBooks(PageRequest.of(0, 10)).getContent();
        stats.put("recentBooks", recentBooks);
        
        return ResponseEntity.ok(stats);
    }
}