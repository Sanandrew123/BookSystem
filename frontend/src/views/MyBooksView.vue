<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-serif font-bold text-gray-900">My Books</h1>
      <p class="mt-2 text-gray-600">Manage your borrowed books and reading history</p>
    </div>

    <!-- Tabs -->
    <div class="mb-8">
      <nav class="flex space-x-8">
        <button v-for="tab in tabs" :key="tab.key"
                @click="activeTab = tab.key"
                :class="activeTab === tab.key ? 'border-primary-500 text-primary-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
                class="whitespace-nowrap py-2 px-1 border-b-2 font-medium text-sm">
          {{ tab.label }}
          <span v-if="tab.count !== undefined" class="ml-2 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                :class="activeTab === tab.key ? 'bg-primary-100 text-primary-800' : 'bg-gray-100 text-gray-800'">
            {{ tab.count }}
          </span>
        </button>
      </nav>
    </div>

    <!-- Filters for active tab -->
    <div v-if="activeTab !== 'history'" class="card mb-6">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <input v-model="searchQuery" type="text" class="input-field pl-10 w-64" 
                   placeholder="Search your books..." @input="debouncedSearch">
          </div>
          <select v-model="sortBy" @change="loadBorrowRecords" class="input-field w-48">
            <option value="createdAt">Sort by Borrow Date</option>
            <option value="dueDate">Sort by Due Date</option>
            <option value="book.title">Sort by Title</option>
            <option value="book.author">Sort by Author</option>
          </select>
        </div>
        <div class="text-sm text-gray-500">
          {{ totalElements }} books
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
    </div>

    <!-- Borrowed Books Content -->
    <div v-else-if="borrowRecords.length > 0" class="space-y-4">
      <div v-for="record in borrowRecords" :key="record.id" class="card hover:shadow-md transition-shadow">
        <div class="flex items-center space-x-6">
          <!-- Book Cover -->
          <div class="w-16 h-20 bg-gradient-to-br from-accent-400 to-primary-500 rounded-lg flex items-center justify-center flex-shrink-0 cursor-pointer"
               @click="openBookDetail(record.book)">
            <img v-if="record.book.coverImageUrl" :src="record.book.coverImageUrl" :alt="record.book.title" 
                 class="w-full h-full object-cover rounded-lg">
            <BookOpenIcon v-else class="h-8 w-8 text-white" />
          </div>
          
          <!-- Book Info -->
          <div class="flex-1 min-w-0">
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <h3 class="text-lg font-serif font-semibold text-gray-900 cursor-pointer hover:text-primary-600"
                    @click="openBookDetail(record.book)">
                  {{ record.book.title }}
                </h3>
                <p class="text-gray-600">{{ record.book.author }}</p>
                <p class="text-sm text-gray-500">{{ record.book.category }}</p>
                
                <div class="flex items-center mt-2 space-x-4 text-sm">
                  <div class="flex items-center">
                    <CalendarDaysIcon class="h-4 w-4 text-gray-400 mr-1" />
                    <span class="text-gray-600">Borrowed: {{ formatDate(record.borrowDate) }}</span>
                  </div>
                  
                  <div class="flex items-center">
                    <ClockIcon class="h-4 w-4 text-gray-400 mr-1" />
                    <span :class="getDueDateColor(record.dueDate, record.status)">
                      Due: {{ formatDate(record.dueDate) }}
                      <span v-if="isOverdue(record.dueDate, record.status)" class="font-medium">
                        ({{ getDaysOverdue(record.dueDate) }} days overdue)
                      </span>
                    </span>
                  </div>
                </div>
                
                <div v-if="record.notes" class="mt-2">
                  <p class="text-sm text-gray-600">
                    <span class="font-medium">Notes:</span> {{ record.notes }}
                  </p>
                </div>
              </div>
              
              <!-- Actions and Status -->
              <div class="text-right ml-4 flex-shrink-0 space-y-2">
                <div>
                  <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                        :class="getStatusColor(record.status)">
                    {{ record.status }}
                  </span>
                </div>
                
                <div v-if="record.fineAmount > 0" class="text-sm">
                  <span class="text-danger-600 font-medium">Fine: ${{ record.fineAmount.toFixed(2) }}</span>
                </div>
                
                <div class="flex flex-col space-y-2">
                  <button v-if="record.status === 'BORROWED' && !isOverdue(record.dueDate, record.status)" 
                          @click="renewBook(record)"
                          class="btn-outline text-xs px-3 py-1">
                    Renew
                  </button>
                  
                  <button v-if="record.status === 'BORROWED'" 
                          @click="returnBook(record)"
                          class="btn-primary text-xs px-3 py-1">
                    Return
                  </button>
                  
                  <button v-if="record.status === 'RETURNED'" 
                          @click="borrowAgain(record.book)"
                          class="btn-secondary text-xs px-3 py-1">
                    Borrow Again
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Pagination -->
      <div class="flex items-center justify-between mt-8">
        <div class="text-sm text-gray-700">
          Showing {{ ((currentPage - 1) * pageSize) + 1 }} to {{ Math.min(currentPage * pageSize, totalElements) }} 
          of {{ totalElements }} results
        </div>
        <div class="flex space-x-2">
          <button @click="goToPage(currentPage - 1)" :disabled="currentPage <= 1" 
                  class="btn-outline px-4 py-2 disabled:opacity-50">
            Previous
          </button>
          <span class="px-4 py-2 text-sm text-gray-700">
            Page {{ currentPage }} of {{ totalPages }}
          </span>
          <button @click="goToPage(currentPage + 1)" :disabled="currentPage >= totalPages" 
                  class="btn-outline px-4 py-2 disabled:opacity-50">
            Next
          </button>
        </div>
      </div>
    </div>

    <!-- No Results -->
    <div v-else class="text-center py-12">
      <BookOpenIcon class="mx-auto h-12 w-12 text-gray-400" />
      <h3 class="mt-2 text-sm font-medium text-gray-900">
        {{ getEmptyStateMessage() }}
      </h3>
      <p class="mt-1 text-sm text-gray-500">
        {{ getEmptyStateDescription() }}
      </p>
      <router-link to="/books" class="mt-4 btn-primary inline-flex items-center">
        <MagnifyingGlassIcon class="h-5 w-5 mr-2" />
        Browse Books
      </router-link>
    </div>

    <!-- Book Detail Modal -->
    <BookDetailModal 
      v-if="selectedBook" 
      :book="selectedBook" 
      :isOpen="showBookDetail"
      @close="closeBookDetail"
      @borrow="handleBorrowFromModal"
    />

    <!-- Return Book Modal -->
    <div v-if="showReturnModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" @click="closeReturnModal"></div>
        
        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6">
          <div>
            <h3 class="text-lg leading-6 font-serif font-semibold text-gray-900 mb-4">
              Return Book
            </h3>
            
            <div class="mb-4">
              <p class="text-sm text-gray-600">
                Are you sure you want to return "<strong>{{ bookToReturn?.book.title }}</strong>"?
              </p>
            </div>
            
            <div class="mb-4">
              <label class="form-label">Notes (Optional)</label>
              <textarea v-model="returnNotes" class="input-field" rows="3" 
                        placeholder="Any comments about the book condition or your reading experience..."></textarea>
            </div>
            
            <div class="flex justify-end space-x-3">
              <button @click="closeReturnModal" class="btn-outline">
                Cancel
              </button>
              <button @click="confirmReturn" :disabled="returning" class="btn-primary">
                {{ returning ? 'Returning...' : 'Return Book' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import type { Book, BorrowRecord } from '../types'
import BookDetailModal from '../components/BookDetailModal.vue'
import {
  MagnifyingGlassIcon,
  BookOpenIcon,
  CalendarDaysIcon,
  ClockIcon
} from '@heroicons/vue/24/outline'

const borrowRecords = ref<BorrowRecord[]>([])
const loading = ref(true)
const returning = ref(false)

const activeTab = ref('current')
const searchQuery = ref('')
const sortBy = ref('createdAt')

const currentPage = ref(1)
const pageSize = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)

const selectedBook = ref<Book | null>(null)
const showBookDetail = ref(false)

const showReturnModal = ref(false)
const bookToReturn = ref<BorrowRecord | null>(null)
const returnNotes = ref('')

// Stats for tab counts
const currentCount = ref(0)
const overdueCount = ref(0)
const historyCount = ref(0)

let searchTimeout: NodeJS.Timeout

const tabs = computed(() => [
  { key: 'current', label: 'Currently Borrowed', count: currentCount.value },
  { key: 'overdue', label: 'Overdue', count: overdueCount.value },
  { key: 'history', label: 'Reading History', count: historyCount.value }
])

const loadBorrowRecords = async () => {
  try {
    loading.value = true
    const params = new URLSearchParams({
      page: (currentPage.value - 1).toString(),
      size: pageSize.value.toString(),
      sortBy: sortBy.value,
      sortDir: 'desc'
    })
    
    if (searchQuery.value.trim()) {
      // For search, we'll search across all records and filter client-side
      // In a real app, you'd want server-side filtering
    }
    
    // Filter by status based on active tab
    if (activeTab.value === 'current') {
      params.append('status', 'BORROWED')
    } else if (activeTab.value === 'overdue') {
      params.append('status', 'OVERDUE')
    }
    // For history, we get all records
    
    const response = await axios.get(`/api/borrows/my-books?${params}`)
    let records = response.data.content
    
    // Client-side filtering for search and status
    if (searchQuery.value.trim()) {
      const query = searchQuery.value.toLowerCase()
      records = records.filter((record: BorrowRecord) => 
        record.book.title.toLowerCase().includes(query) ||
        record.book.author.toLowerCase().includes(query)
      )
    }
    
    if (activeTab.value === 'history') {
      records = records.filter((record: BorrowRecord) => 
        record.status === 'RETURNED' || record.status === 'LOST'
      )
    }
    
    borrowRecords.value = records
    totalElements.value = records.length
    totalPages.value = Math.ceil(records.length / pageSize.value)
  } catch (error) {
    console.error('Failed to load borrow records:', error)
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    // Load counts for each tab
    const [currentResponse, overdueResponse, historyResponse] = await Promise.all([
      axios.get('/api/borrows/my-books?status=BORROWED&size=1'),
      axios.get('/api/borrows/my-books?status=OVERDUE&size=1'),
      axios.get('/api/borrows/my-books?size=1') // Get total and calculate history
    ])
    
    currentCount.value = currentResponse.data.totalElements
    overdueCount.value = overdueResponse.data.totalElements
    
    // Calculate history count (returned + lost books)
    const allResponse = await axios.get('/api/borrows/my-books?size=1000') // Get all to count
    historyCount.value = allResponse.data.content.filter((record: BorrowRecord) => 
      record.status === 'RETURNED' || record.status === 'LOST'
    ).length
  } catch (error) {
    console.error('Failed to load stats:', error)
  }
}

const debouncedSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    currentPage.value = 1
    loadBorrowRecords()
  }, 500)
}

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    loadBorrowRecords()
  }
}

const openBookDetail = (book: Book) => {
  selectedBook.value = book
  showBookDetail.value = true
}

const closeBookDetail = () => {
  showBookDetail.value = false
  selectedBook.value = null
}

const renewBook = async (record: BorrowRecord) => {
  try {
    await axios.post(`/api/borrows/${record.id}/renew`, { additionalDays: 14 })
    loadBorrowRecords()
    loadStats()
    alert('Book renewed successfully!')
  } catch (error: any) {
    alert(error.response?.data?.message || 'Failed to renew book')
  }
}

const returnBook = (record: BorrowRecord) => {
  bookToReturn.value = record
  showReturnModal.value = true
}

const confirmReturn = async () => {
  if (!bookToReturn.value) return
  
  try {
    returning.value = true
    await axios.post(`/api/borrows/${bookToReturn.value.id}/return`, {
      notes: returnNotes.value
    })
    
    closeReturnModal()
    loadBorrowRecords()
    loadStats()
    alert('Book returned successfully!')
  } catch (error: any) {
    alert(error.response?.data?.message || 'Failed to return book')
  } finally {
    returning.value = false
  }
}

const closeReturnModal = () => {
  showReturnModal.value = false
  bookToReturn.value = null
  returnNotes.value = ''
}

const borrowAgain = async (book: Book) => {
  try {
    const dueDate = new Date()
    dueDate.setDate(dueDate.getDate() + 14)
    
    await axios.post('/api/borrows/borrow', {
      bookId: book.id,
      dueDate: dueDate.toISOString().split('T')[0]
    })
    
    loadBorrowRecords()
    loadStats()
    alert(`"${book.title}" has been borrowed again!`)
  } catch (error: any) {
    alert(error.response?.data?.message || 'Failed to borrow book')
  }
}

const handleBorrowFromModal = (book: Book) => {
  borrowAgain(book)
  closeBookDetail()
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

const isOverdue = (dueDateString: string, status: string) => {
  return status === 'OVERDUE' || (status === 'BORROWED' && new Date() > new Date(dueDateString))
}

const getDaysOverdue = (dueDateString: string) => {
  const dueDate = new Date(dueDateString)
  const today = new Date()
  const diffTime = today.getTime() - dueDate.getTime()
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
}

const getDueDateColor = (dueDateString: string, status: string) => {
  if (isOverdue(dueDateString, status)) {
    return 'text-danger-600 font-medium'
  }
  
  const dueDate = new Date(dueDateString)
  const today = new Date()
  const daysUntilDue = Math.ceil((dueDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))
  
  if (daysUntilDue <= 3) {
    return 'text-orange-600 font-medium'
  }
  
  return 'text-gray-600'
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'BORROWED':
      return 'bg-primary-100 text-primary-800'
    case 'RETURNED':
      return 'bg-success-100 text-success-800'
    case 'OVERDUE':
      return 'bg-danger-100 text-danger-800'
    case 'LOST':
      return 'bg-gray-100 text-gray-800'
    default:
      return 'bg-secondary-100 text-secondary-800'
  }
}

const getEmptyStateMessage = () => {
  switch (activeTab.value) {
    case 'current':
      return 'No books currently borrowed'
    case 'overdue':
      return 'No overdue books'
    case 'history':
      return 'No reading history'
    default:
      return 'No books found'
  }
}

const getEmptyStateDescription = () => {
  switch (activeTab.value) {
    case 'current':
      return 'Browse our catalog to find your next great read'
    case 'overdue':
      return 'Great! All your books are returned on time'
    case 'history':
      return 'Your returned books will appear here'
    default:
      return 'Try adjusting your search or browse all books'
  }
}

watch(activeTab, () => {
  currentPage.value = 1
  searchQuery.value = ''
  loadBorrowRecords()
})

onMounted(() => {
  loadBorrowRecords()
  loadStats()
})
</script>