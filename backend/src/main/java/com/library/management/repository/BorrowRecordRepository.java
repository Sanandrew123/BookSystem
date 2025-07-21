package com.library.management.repository;

import com.library.management.entity.BorrowRecord;
import com.library.management.entity.User;
import com.library.management.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    
    Page<BorrowRecord> findByUser(User user, Pageable pageable);
    
    Page<BorrowRecord> findByBook(Book book, Pageable pageable);
    
    Page<BorrowRecord> findByStatus(BorrowRecord.BorrowStatus status, Pageable pageable);
    
    Page<BorrowRecord> findByUserAndStatus(User user, BorrowRecord.BorrowStatus status, Pageable pageable);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.user = :user AND br.book = :book AND br.status = 'BORROWED'")
    Optional<BorrowRecord> findActiveBorrowRecord(@Param("user") User user, @Param("book") Book book);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWED' AND br.dueDate < :currentDate")
    List<BorrowRecord> findOverdueRecords(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWED' AND br.dueDate BETWEEN :startDate AND :endDate")
    List<BorrowRecord> findRecordsDueSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.status = 'BORROWED'")
    long countActiveBorrows();
    
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.status = 'OVERDUE'")
    long countOverdueBooks();
    
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.user = :user AND br.status = 'BORROWED'")
    long countActiveBorrowsByUser(@Param("user") User user);
    
    @Query("SELECT br FROM BorrowRecord br ORDER BY br.createdAt DESC")
    Page<BorrowRecord> findRecentActivity(Pageable pageable);
    
    @Query("SELECT br FROM BorrowRecord br WHERE " +
           "LOWER(br.user.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(br.user.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(br.book.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(br.book.author) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<BorrowRecord> findBySearchTerm(@Param("search") String search, Pageable pageable);
}