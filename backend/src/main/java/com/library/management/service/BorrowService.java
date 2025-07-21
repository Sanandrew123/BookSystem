package com.library.management.service;

import com.library.management.entity.Book;
import com.library.management.entity.BorrowRecord;
import com.library.management.entity.User;
import com.library.management.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BorrowService {
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private UserService userService;
    
    private static final int MAX_BORROW_LIMIT = 5;
    private static final int DEFAULT_BORROW_DAYS = 14;
    private static final double FINE_PER_DAY = 1.0;
    
    public BorrowRecord borrowBook(Long userId, Long bookId, LocalDate dueDate) {
        User user = userService.findById(userId);
        Book book = bookService.findById(bookId);
        
        // Check if user already has this book borrowed
        if (borrowRecordRepository.findActiveBorrowRecord(user, book).isPresent()) {
            throw new RuntimeException("User already has this book borrowed");
        }
        
        // Check borrow limit
        long activeBorrows = borrowRecordRepository.countActiveBorrowsByUser(user);
        if (activeBorrows >= MAX_BORROW_LIMIT) {
            throw new RuntimeException("User has reached maximum borrow limit");
        }
        
        // Check if book is available
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No copies available for borrowing");
        }
        
        // Create borrow record
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUser(user);
        borrowRecord.setBook(book);
        borrowRecord.setBorrowDate(LocalDate.now());
        borrowRecord.setDueDate(dueDate != null ? dueDate : LocalDate.now().plusDays(DEFAULT_BORROW_DAYS));
        borrowRecord.setStatus(BorrowRecord.BorrowStatus.BORROWED);
        
        // Update book availability
        bookService.borrowBook(bookId);
        
        return borrowRecordRepository.save(borrowRecord);
    }
    
    public BorrowRecord returnBook(Long borrowRecordId, String notes) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));
        
        if (borrowRecord.getStatus() != BorrowRecord.BorrowStatus.BORROWED) {
            throw new RuntimeException("Book is not currently borrowed");
        }
        
        // Calculate fine if overdue
        LocalDate returnDate = LocalDate.now();
        double fineAmount = 0.0;
        
        if (returnDate.isAfter(borrowRecord.getDueDate())) {
            long daysOverdue = returnDate.toEpochDay() - borrowRecord.getDueDate().toEpochDay();
            fineAmount = daysOverdue * FINE_PER_DAY;
        }
        
        // Update borrow record
        borrowRecord.setReturnDate(returnDate);
        borrowRecord.setStatus(BorrowRecord.BorrowStatus.RETURNED);
        borrowRecord.setFineAmount(fineAmount);
        borrowRecord.setNotes(notes);
        
        // Update book availability
        bookService.returnBook(borrowRecord.getBook().getId());
        
        return borrowRecordRepository.save(borrowRecord);
    }
    
    public BorrowRecord renewBook(Long borrowRecordId, int additionalDays) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));
        
        if (borrowRecord.getStatus() != BorrowRecord.BorrowStatus.BORROWED) {
            throw new RuntimeException("Book is not currently borrowed");
        }
        
        // Check if book is overdue
        if (LocalDate.now().isAfter(borrowRecord.getDueDate())) {
            throw new RuntimeException("Cannot renew overdue book");
        }
        
        // Extend due date
        borrowRecord.setDueDate(borrowRecord.getDueDate().plusDays(additionalDays));
        
        return borrowRecordRepository.save(borrowRecord);
    }
    
    public void markAsLost(Long borrowRecordId, String notes) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));
        
        borrowRecord.setStatus(BorrowRecord.BorrowStatus.LOST);
        borrowRecord.setNotes(notes);
        
        // Apply fine for lost book (usually the cost of the book)
        borrowRecord.setFineAmount(50.0); // Default fine for lost book
        
        borrowRecordRepository.save(borrowRecord);
    }
    
    public BorrowRecord findById(Long id) {
        return borrowRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));
    }
    
    public Page<BorrowRecord> findAllBorrowRecords(Pageable pageable) {
        return borrowRecordRepository.findAll(pageable);
    }
    
    public Page<BorrowRecord> findBorrowRecordsByUser(User user, Pageable pageable) {
        return borrowRecordRepository.findByUser(user, pageable);
    }
    
    public Page<BorrowRecord> findBorrowRecordsByStatus(BorrowRecord.BorrowStatus status, Pageable pageable) {
        return borrowRecordRepository.findByStatus(status, pageable);
    }
    
    public Page<BorrowRecord> searchBorrowRecords(String search, Pageable pageable) {
        return borrowRecordRepository.findBySearchTerm(search, pageable);
    }
    
    public Page<BorrowRecord> getRecentActivity(Pageable pageable) {
        return borrowRecordRepository.findRecentActivity(pageable);
    }
    
    public List<BorrowRecord> getOverdueRecords() {
        return borrowRecordRepository.findOverdueRecords(LocalDate.now());
    }
    
    public List<BorrowRecord> getRecordsDueSoon(int days) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);
        return borrowRecordRepository.findRecordsDueSoon(startDate, endDate);
    }
    
    public void updateOverdueStatus() {
        List<BorrowRecord> overdueRecords = getOverdueRecords();
        for (BorrowRecord record : overdueRecords) {
            if (record.getStatus() == BorrowRecord.BorrowStatus.BORROWED) {
                record.setStatus(BorrowRecord.BorrowStatus.OVERDUE);
                borrowRecordRepository.save(record);
            }
        }
    }
    
    public long getActiveBorrowCount() {
        return borrowRecordRepository.countActiveBorrows();
    }
    
    public long getOverdueCount() {
        return borrowRecordRepository.countOverdueBooks();
    }
}