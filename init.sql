-- Initialize the database with some sample data
USE library_db;

-- Insert sample users (passwords are 'password' encoded with BCrypt)
INSERT INTO users (username, email, password, first_name, last_name, phone_number, role, enabled, created_at, updated_at) VALUES
('admin', 'admin@library.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Library', 'Administrator', '+1-555-0001', 'ADMIN', true, NOW(), NOW()),
('librarian', 'librarian@library.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Jane', 'Librarian', '+1-555-0002', 'LIBRARIAN', true, NOW(), NOW()),
('student1', 'student1@university.edu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'John', 'Student', '+1-555-0003', 'STUDENT', true, NOW(), NOW()),
('student2', 'student2@university.edu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Alice', 'Johnson', '+1-555-0004', 'STUDENT', true, NOW(), NOW());

-- Insert sample books
INSERT INTO books (title, author, isbn, publisher, publication_date, category, description, total_copies, available_copies, cover_image_url, rating, review_count, status, created_at, updated_at) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Scribner', '1925-04-10', 'Fiction', 'A classic American novel set in the Jazz Age, exploring themes of decadence, idealism, resistance to change, social upheaval, and excess.', 5, 5, 'https://covers.openlibrary.org/b/isbn/9780743273565-L.jpg', 4.5, 1250, 'AVAILABLE', NOW(), NOW()),

('To Kill a Mockingbird', 'Harper Lee', '9780061120084', 'J.B. Lippincott & Co.', '1960-07-11', 'Fiction', 'A gripping tale of racial injustice and childhood innocence in the American South.', 3, 3, 'https://covers.openlibrary.org/b/isbn/9780061120084-L.jpg', 4.8, 2100, 'AVAILABLE', NOW(), NOW()),

('1984', 'George Orwell', '9780451524935', 'Secker & Warburg', '1949-06-08', 'Science Fiction', 'A dystopian social science fiction novel about totalitarian control and surveillance.', 4, 4, 'https://covers.openlibrary.org/b/isbn/9780451524935-L.jpg', 4.7, 1890, 'AVAILABLE', NOW(), NOW()),

('Pride and Prejudice', 'Jane Austen', '9780141439518', 'T. Egerton', '1813-01-28', 'Romance', 'A romantic novel that critiques the British landed gentry at the end of the 18th century.', 2, 2, 'https://covers.openlibrary.org/b/isbn/9780141439518-L.jpg', 4.6, 1650, 'AVAILABLE', NOW(), NOW()),

('The Catcher in the Rye', 'J.D. Salinger', '9780316769174', 'Little, Brown and Company', '1951-07-16', 'Fiction', 'A controversial novel about teenage rebellion and alienation in post-war America.', 3, 2, 'https://covers.openlibrary.org/b/isbn/9780316769174-L.jpg', 4.2, 980, 'AVAILABLE', NOW(), NOW()),

('Introduction to Algorithms', 'Thomas H. Cormen', '9780262033848', 'MIT Press', '2009-07-31', 'Computer Science', 'A comprehensive textbook on computer algorithms used in universities worldwide.', 6, 6, 'https://covers.openlibrary.org/b/isbn/9780262033848-L.jpg', 4.9, 450, 'AVAILABLE', NOW(), NOW()),

('Clean Code', 'Robert C. Martin', '9780132350884', 'Prentice Hall', '2008-08-01', 'Computer Science', 'A handbook of agile software craftsmanship with practical advice for writing clean code.', 4, 3, 'https://covers.openlibrary.org/b/isbn/9780132350884-L.jpg', 4.7, 320, 'AVAILABLE', NOW(), NOW()),

('The Elements of Style', 'William Strunk Jr.', '9780205313426', 'Harcourt', '1918-01-01', 'Reference', 'A prescriptive American English writing style guide with rules of usage.', 5, 5, 'https://covers.openlibrary.org/b/isbn/9780205313426-L.jpg', 4.3, 180, 'AVAILABLE', NOW(), NOW()),

('A Brief History of Time', 'Stephen Hawking', '9780553380163', 'Bantam Books', '1988-04-01', 'Science', 'A landmark volume in science writing that explains complex cosmological concepts for general readers.', 3, 3, 'https://covers.openlibrary.org/b/isbn/9780553380163-L.jpg', 4.5, 890, 'AVAILABLE', NOW(), NOW()),

('The Art of War', 'Sun Tzu', '9781599869773', 'Ancient Chinese', '0500-01-01', 'Philosophy', 'An ancient Chinese military treatise with strategies applicable to business and life.', 2, 2, 'https://covers.openlibrary.org/b/isbn/9781599869773-L.jpg', 4.4, 520, 'AVAILABLE', NOW(), NOW());

-- Insert sample borrow records
INSERT INTO borrow_records (user_id, book_id, borrow_date, due_date, return_date, status, fine_amount, notes, created_at, updated_at) VALUES
-- Current borrows
(3, 5, CURDATE() - INTERVAL 7 DAY, CURDATE() + INTERVAL 7 DAY, NULL, 'BORROWED', 0.00, 'Great read so far!', NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY),
(4, 7, CURDATE() - INTERVAL 3 DAY, CURDATE() + INTERVAL 11 DAY, NULL, 'BORROWED', 0.00, 'Very helpful for my programming course.', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY),

-- Returned books
(3, 1, CURDATE() - INTERVAL 21 DAY, CURDATE() - INTERVAL 7 DAY, CURDATE() - INTERVAL 5 DAY, 'RETURNED', 0.00, 'Excellent classic! Highly recommend.', NOW() - INTERVAL 21 DAY, NOW() - INTERVAL 5 DAY),
(4, 2, CURDATE() - INTERVAL 30 DAY, CURDATE() - INTERVAL 16 DAY, CURDATE() - INTERVAL 14 DAY, 'RETURNED', 0.00, 'Powerful and moving story.', NOW() - INTERVAL 30 DAY, NOW() - INTERVAL 14 DAY);