export interface User {
  id: number
  username: string
  email: string
  firstName: string
  lastName: string
  phoneNumber: string
  role: 'ADMIN' | 'LIBRARIAN' | 'STUDENT'
  enabled: boolean
  createdAt: string
  updatedAt: string
}

export interface Book {
  id: number
  title: string
  author: string
  isbn: string
  publisher: string
  publicationDate: string
  category: string
  description?: string
  totalCopies: number
  availableCopies: number
  coverImageUrl?: string
  rating: number
  reviewCount: number
  status: 'AVAILABLE' | 'OUT_OF_STOCK' | 'DISCONTINUED'
  createdAt: string
  updatedAt: string
}

export interface BorrowRecord {
  id: number
  user: User
  book: Book
  borrowDate: string
  dueDate: string
  returnDate?: string
  status: 'BORROWED' | 'RETURNED' | 'OVERDUE' | 'LOST'
  fineAmount: number
  notes?: string
  createdAt: string
  updatedAt: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  firstName: string
  lastName: string
  phoneNumber: string
}

export interface BookCreateRequest {
  title: string
  author: string
  isbn: string
  publisher: string
  publicationDate: string
  category: string
  description?: string
  totalCopies: number
  coverImageUrl?: string
}

export interface BookUpdateRequest extends Partial<BookCreateRequest> {
  id: number
}

export interface BorrowRequest {
  bookId: number
  dueDate: string
}

export interface ReturnRequest {
  borrowRecordId: number
  notes?: string
}

export interface SearchFilters {
  query?: string
  category?: string
  author?: string
  availableOnly?: boolean
  sortBy?: 'title' | 'author' | 'publicationDate' | 'rating'
  sortOrder?: 'asc' | 'desc'
}

export interface PaginationParams {
  page: number
  size: number
}

export interface ApiResponse<T> {
  data: T
  message?: string
  success: boolean
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

export interface DashboardStats {
  totalBooks: number
  totalUsers: number
  activeBorrows: number
  overdueBooks: number
  popularBooks: Book[]
  recentActivity: BorrowRecord[]
}