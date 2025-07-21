package com.library.management.controller;

import com.library.management.entity.BorrowRecord;
import com.library.management.entity.User;
import com.library.management.service.BorrowService;
import com.library.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrows")
@CrossOrigin(origins = "*")
public class BorrowController {
    
    @Autowired
    private BorrowService borrowService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Page<BorrowRecord>> getAllBorrowRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BorrowRecord> borrowRecords;
        
        if (search != null && !search.trim().isEmpty()) {
            borrowRecords = borrowService.searchBorrowRecords(search.trim(), pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            try {
                BorrowRecord.BorrowStatus borrowStatus = BorrowRecord.BorrowStatus.valueOf(status.toUpperCase());
                borrowRecords = borrowService.findBorrowRecordsByStatus(borrowStatus, pageable);
            } catch (IllegalArgumentException e) {
                borrowRecords = borrowService.findAllBorrowRecords(pageable);
            }
        } else {
            borrowRecords = borrowService.findAllBorrowRecords(pageable);
        }
        
        return ResponseEntity.ok(borrowRecords);
    }
    
    @GetMapping("/my-books")
    public ResponseEntity<Page<BorrowRecord>> getMyBorrowRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BorrowRecord> borrowRecords;
        
        if (status != null && !status.trim().isEmpty()) {
            try {
                BorrowRecord.BorrowStatus borrowStatus = BorrowRecord.BorrowStatus.valueOf(status.toUpperCase());
                borrowRecords = borrowService.findBorrowRecordsByUser(currentUser, pageable);
                // Filter by status would need a custom query in service layer
            } catch (IllegalArgumentException e) {
                borrowRecords = borrowService.findBorrowRecordsByUser(currentUser, pageable);
            }
        } else {
            borrowRecords = borrowService.findBorrowRecordsByUser(currentUser, pageable);
        }
        
        return ResponseEntity.ok(borrowRecords);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BorrowRecord> getBorrowRecordById(@PathVariable Long id) {
        try {
            BorrowRecord borrowRecord = borrowService.findById(id);
            
            // Check if user can access this record
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            
            if (!currentUser.getRole().equals(User.Role.ADMIN) && 
                !currentUser.getRole().equals(User.Role.LIBRARIAN) &&
                !borrowRecord.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.forbidden().build();
            }
            
            return ResponseEntity.ok(borrowRecord);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@Valid @RequestBody BorrowBookRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            
            BorrowRecord borrowRecord = borrowService.borrowBook(
                currentUser.getId(), 
                request.getBookId(), 
                request.getDueDate()
            );
            
            return ResponseEntity.ok(borrowRecord);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnBook(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> request) {
        try {
            String notes = request != null ? request.get("notes") : null;
            BorrowRecord borrowRecord = borrowService.returnBook(id, notes);
            return ResponseEntity.ok(borrowRecord);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/{id}/renew")
    public ResponseEntity<?> renewBook(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer additionalDays = request.get("additionalDays");
            if (additionalDays == null || additionalDays <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid number of additional days");
                return ResponseEntity.badRequest().body(error);
            }
            
            BorrowRecord borrowRecord = borrowService.renewBook(id, additionalDays);
            return ResponseEntity.ok(borrowRecord);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/{id}/mark-lost")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<?> markAsLost(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> request) {
        try {
            String notes = request != null ? request.get("notes") : null;
            borrowService.markAsLost(id, notes);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Book marked as lost successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<List<BorrowRecord>> getOverdueRecords() {
        return ResponseEntity.ok(borrowService.getOverdueRecords());
    }
    
    @GetMapping("/due-soon")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<List<BorrowRecord>> getRecordsDueSoon(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(borrowService.getRecordsDueSoon(days));
    }
    
    @PostMapping("/update-overdue-status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<?> updateOverdueStatus() {
        borrowService.updateOverdueStatus();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Overdue status updated successfully");
        return ResponseEntity.ok(response);
    }
    
    public static class BorrowBookRequest {
        private Long bookId;
        private LocalDate dueDate;
        
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        
        public LocalDate getDueDate() { return dueDate; }
        public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    }
}