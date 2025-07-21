package com.library.management.service;

import com.library.management.entity.Book;
import com.library.management.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public Book createBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new RuntimeException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        
        book.setAvailableCopies(book.getTotalCopies());
        return bookRepository.save(book);
    }
    
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setPublisher(bookDetails.getPublisher());
        book.setPublicationDate(bookDetails.getPublicationDate());
        book.setCategory(bookDetails.getCategory());
        book.setDescription(bookDetails.getDescription());
        book.setCoverImageUrl(bookDetails.getCoverImageUrl());
        
        // Update total copies and adjust available copies
        int copiesDifference = bookDetails.getTotalCopies() - book.getTotalCopies();
        book.setTotalCopies(bookDetails.getTotalCopies());
        book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + copiesDifference));
        
        book.setStatus(bookDetails.getStatus());
        
        return bookRepository.save(book);
    }
    
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        // Check if book has any active borrows
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            throw new RuntimeException("Cannot delete book with active borrows");
        }
        
        bookRepository.deleteById(id);
    }
    
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }
    
    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }
    
    public Page<Book> findAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
    
    public Page<Book> searchBooks(String search, Pageable pageable) {
        return bookRepository.findBySearchTerm(search, pageable);
    }
    
    public Page<Book> findBooksByCategory(String category, Pageable pageable) {
        return bookRepository.findByCategory(category, pageable);
    }
    
    public Page<Book> findBooksByAuthor(String author, Pageable pageable) {
        return bookRepository.findByAuthor(author, pageable);
    }
    
    public Page<Book> findAvailableBooks(Pageable pageable) {
        return bookRepository.findAvailableBooks(pageable);
    }
    
    public Page<Book> findTopRatedBooks(Pageable pageable) {
        return bookRepository.findTopRatedBooks(pageable);
    }
    
    public Page<Book> findMostReviewedBooks(Pageable pageable) {
        return bookRepository.findMostReviewedBooks(pageable);
    }
    
    public List<String> getAllCategories() {
        return bookRepository.findAllCategories();
    }
    
    public List<String> getAllAuthors() {
        return bookRepository.findAllAuthors();
    }
    
    public void updateBookCopies(Long bookId, int totalCopies) {
        Book book = findById(bookId);
        int copiesDifference = totalCopies - book.getTotalCopies();
        
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + copiesDifference));
        
        bookRepository.save(book);
    }
    
    public void borrowBook(Long bookId) {
        Book book = findById(bookId);
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No copies available for borrowing");
        }
        
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }
    
    public void returnBook(Long bookId) {
        Book book = findById(bookId);
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new RuntimeException("All copies are already available");
        }
        
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }
    
    public long getTotalBookCount() {
        return bookRepository.count();
    }
    
    public long getAvailableBookCount() {
        return bookRepository.countAvailableBooks();
    }
}