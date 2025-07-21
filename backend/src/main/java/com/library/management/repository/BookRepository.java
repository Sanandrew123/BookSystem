package com.library.management.repository;

import com.library.management.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    Optional<Book> findByIsbn(String isbn);
    
    boolean existsByIsbn(String isbn);
    
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.category) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Book> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    Page<Book> findByCategory(String category, Pageable pageable);
    
    Page<Book> findByAuthor(String author, Pageable pageable);
    
    Page<Book> findByAvailableCopiesGreaterThan(Integer copies, Pageable pageable);
    
    Page<Book> findByStatus(Book.BookStatus status, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    Page<Book> findAvailableBooks(Pageable pageable);
    
    @Query("SELECT b FROM Book b ORDER BY b.rating DESC")
    Page<Book> findTopRatedBooks(Pageable pageable);
    
    @Query("SELECT b FROM Book b ORDER BY b.reviewCount DESC")
    Page<Book> findMostReviewedBooks(Pageable pageable);
    
    @Query("SELECT DISTINCT b.category FROM Book b ORDER BY b.category")
    List<String> findAllCategories();
    
    @Query("SELECT DISTINCT b.author FROM Book b ORDER BY b.author")
    List<String> findAllAuthors();
    
    @Query("SELECT COUNT(b) FROM Book b WHERE b.status = 'AVAILABLE'")
    long countAvailableBooks();
    
    @Query("SELECT COUNT(b) FROM Book b")
    long countTotalBooks();
}