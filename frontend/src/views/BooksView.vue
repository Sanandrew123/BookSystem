<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-serif font-bold text-gray-900">Library Catalog</h1>
      <p class="mt-2 text-gray-600">Discover your next great read</p>
    </div>

    <!-- Search and Filters -->
    <div class="card mb-8">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div class="md:col-span-2">
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <input
              v-model="searchQuery"
              type="text"
              class="input-field pl-10"
              placeholder="Search books, authors, ISBN..."
              @input="debouncedSearch"
            />
          </div>
        </div>
        <div>
          <select v-model="selectedCategory" @change="applyFilters" class="input-field">
            <option value="">All Categories</option>
            <option v-for="category in categories" :key="category" :value="category">
              {{ category }}
            </option>
          </select>
        </div>
        <div>
          <select v-model="sortBy" @change="applyFilters" class="input-field">
            <option value="title">Sort by Title</option>
            <option value="author">Sort by Author</option>
            <option value="rating">Sort by Rating</option>
            <option value="publicationDate">Sort by Date</option>
          </select>
        </div>
      </div>
      
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <label class="flex items-center">
            <input v-model="availableOnly" @change="applyFilters" type="checkbox" 
                   class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded">
            <span class="ml-2 text-sm text-gray-700">Available only</span>
          </label>
          <div class="flex items-center space-x-2">
            <button @click="viewMode = 'grid'" 
                    :class="viewMode === 'grid' ? 'text-primary-600' : 'text-gray-400'"
                    class="p-2 hover:text-primary-600">
              <Squares2X2Icon class="h-5 w-5" />
            </button>
            <button @click="viewMode = 'list'" 
                    :class="viewMode === 'list' ? 'text-primary-600' : 'text-gray-400'"
                    class="p-2 hover:text-primary-600">
              <ListBulletIcon class="h-5 w-5" />
            </button>
          </div>
        </div>
        <div class="text-sm text-gray-500">
          {{ totalElements }} books found
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
    </div>

    <!-- Books Grid/List -->
    <div v-else-if="books.length > 0">
      <!-- Grid View -->
      <div v-if="viewMode === 'grid'" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 mb-8">
        <div v-for="book in books" :key="book.id" 
             class="book-card-hover card cursor-pointer" 
             @click="openBookDetail(book)">
          <div class="aspect-w-3 aspect-h-4 mb-4">
            <div class="w-full h-48 bg-gradient-to-br from-accent-400 to-primary-500 rounded-lg flex items-center justify-center">
              <img v-if="book.coverImageUrl" :src="book.coverImageUrl" :alt="book.title" 
                   class="w-full h-full object-cover rounded-lg">
              <BookOpenIcon v-else class="h-12 w-12 text-white" />
            </div>
          </div>
          
          <div class="space-y-2">
            <h3 class="font-serif font-semibold text-gray-900 line-clamp-2">{{ book.title }}</h3>
            <p class="text-sm text-gray-600">{{ book.author }}</p>
            <p class="text-xs text-gray-500">{{ book.category }}</p>
            
            <div class="flex items-center">
              <div class="flex items-center">
                <StarIcon v-for="i in 5" :key="i" 
                         class="h-4 w-4" 
                         :class="i <= book.rating ? 'text-yellow-400' : 'text-gray-300'" />
              </div>
              <span class="ml-2 text-sm text-gray-500">({{ book.reviewCount }})</span>
            </div>
            
            <div class="flex items-center justify-between pt-2">
              <span v-if="book.availableCopies > 0" 
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-success-100 text-success-800">
                Available ({{ book.availableCopies }})
              </span>
              <span v-else 
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-danger-100 text-danger-800">
                Out of stock
              </span>
              
              <button v-if="book.availableCopies > 0" 
                      @click.stop="borrowBook(book)"
                      class="btn-primary px-3 py-1 text-xs">
                Borrow
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- List View -->
      <div v-else class="space-y-4 mb-8">
        <div v-for="book in books" :key="book.id" 
             class="card hover:shadow-md transition-shadow cursor-pointer"
             @click="openBookDetail(book)">
          <div class="flex items-center space-x-6">
            <div class="w-16 h-20 bg-gradient-to-br from-accent-400 to-primary-500 rounded-lg flex items-center justify-center flex-shrink-0">
              <img v-if="book.coverImageUrl" :src="book.coverImageUrl" :alt="book.title" 
                   class="w-full h-full object-cover rounded-lg">
              <BookOpenIcon v-else class="h-8 w-8 text-white" />
            </div>
            
            <div class="flex-1 min-w-0">
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <h3 class="text-lg font-serif font-semibold text-gray-900">{{ book.title }}</h3>
                  <p class="text-gray-600">{{ book.author }}</p>
                  <p class="text-sm text-gray-500">{{ book.publisher }} • {{ formatDate(book.publicationDate) }}</p>
                  
                  <div class="flex items-center mt-2">
                    <div class="flex items-center">
                      <StarIcon v-for="i in 5" :key="i" 
                               class="h-4 w-4" 
                               :class="i <= book.rating ? 'text-yellow-400' : 'text-gray-300'" />
                    </div>
                    <span class="ml-2 text-sm text-gray-500">({{ book.reviewCount }} reviews)</span>
                    <span class="mx-2 text-gray-300">•</span>
                    <span class="text-sm text-gray-600">{{ book.category }}</span>
                  </div>
                  
                  <p v-if="book.description" class="text-sm text-gray-600 mt-2 line-clamp-2">
                    {{ book.description }}
                  </p>
                </div>
                
                <div class="text-right ml-4 flex-shrink-0">
                  <div class="mb-3">
                    <span v-if="book.availableCopies > 0" 
                          class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-success-100 text-success-800">
                      Available ({{ book.availableCopies }})
                    </span>
                    <span v-else 
                          class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-danger-100 text-danger-800">
                      Out of stock
                    </span>
                  </div>
                  
                  <button v-if="book.availableCopies > 0" 
                          @click.stop="borrowBook(book)"
                          class="btn-primary">
                    Borrow Book
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div class="flex items-center justify-between">
        <div class="text-sm text-gray-700">
          Showing {{ ((currentPage - 1) * pageSize) + 1 }} to {{ Math.min(currentPage * pageSize, totalElements) }} 
          of {{ totalElements }} results
        </div>
        <div class="flex space-x-2">
          <button @click="goToPage(currentPage - 1)" :disabled="currentPage <= 1" 
                  class="btn-outline px-4 py-2 disabled:opacity-50">
            Previous
          </button>
          
          <div class="flex items-center space-x-1">
            <button v-for="page in visiblePages" :key="page"
                    @click="goToPage(page)"
                    :class="page === currentPage ? 'btn-primary' : 'btn-outline'"
                    class="px-3 py-2 min-w-[40px]">
              {{ page }}
            </button>
          </div>
          
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
      <h3 class="mt-2 text-sm font-medium text-gray-900">No books found</h3>
      <p class="mt-1 text-sm text-gray-500">Try adjusting your search or filters</p>
      <button @click="clearFilters" class="mt-4 btn-primary">
        Clear Filters
      </button>
    </div>

    <!-- Book Detail Modal -->
    <BookDetailModal 
      v-if="selectedBook" 
      :book="selectedBook" 
      :isOpen="showBookDetail"
      @close="closeBookDetail"
      @borrow="handleBorrowFromModal"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import type { Book } from '../types'
import BookDetailModal from '../components/BookDetailModal.vue'
import {
  MagnifyingGlassIcon,
  BookOpenIcon,
  StarIcon,
  Squares2X2Icon,
  ListBulletIcon
} from '@heroicons/vue/24/outline'

const router = useRouter()

const books = ref<Book[]>([])
const categories = ref<string[]>([])
const loading = ref(true)
const searchQuery = ref('')
const selectedCategory = ref('')
const sortBy = ref('title')
const availableOnly = ref(false)
const viewMode = ref<'grid' | 'list'>('grid')

const currentPage = ref(1)
const pageSize = ref(12)
const totalElements = ref(0)
const totalPages = ref(0)

const selectedBook = ref<Book | null>(null)
const showBookDetail = ref(false)

let searchTimeout: NodeJS.Timeout

const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, currentPage.value + 2)
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

const loadBooks = async () => {
  try {
    loading.value = true
    const params = new URLSearchParams({
      page: (currentPage.value - 1).toString(),
      size: pageSize.value.toString(),
      sortBy: sortBy.value,
      sortDir: 'asc'
    })
    
    if (searchQuery.value.trim()) {
      params.append('search', searchQuery.value.trim())
    }
    if (selectedCategory.value) {
      params.append('category', selectedCategory.value)
    }
    if (availableOnly.value) {
      params.append('availableOnly', 'true')
    }
    
    const response = await axios.get(`/api/books?${params}`)
    books.value = response.data.content
    totalElements.value = response.data.totalElements
    totalPages.value = response.data.totalPages
  } catch (error) {
    console.error('Failed to load books:', error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const response = await axios.get('/api/books/categories')
    categories.value = response.data
  } catch (error) {
    console.error('Failed to load categories:', error)
  }
}

const debouncedSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    currentPage.value = 1
    loadBooks()
  }, 500)
}

const applyFilters = () => {
  currentPage.value = 1
  loadBooks()
}

const clearFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = ''
  sortBy.value = 'title'
  availableOnly.value = false
  currentPage.value = 1
  loadBooks()
}

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    loadBooks()
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

const borrowBook = async (book: Book) => {
  try {
    const dueDate = new Date()
    dueDate.setDate(dueDate.getDate() + 14) // 14 days from now
    
    await axios.post('/api/borrows/borrow', {
      bookId: book.id,
      dueDate: dueDate.toISOString().split('T')[0]
    })
    
    // Refresh books to update availability
    loadBooks()
    
    // Show success message (you might want to add a toast/notification system)
    alert(`"${book.title}" has been successfully borrowed!`)
  } catch (error: any) {
    alert(error.response?.data?.message || 'Failed to borrow book')
  }
}

const handleBorrowFromModal = (book: Book) => {
  borrowBook(book)
  closeBookDetail()
}

const formatDate = (dateString: string) => {
  return new Date(dateString).getFullYear()
}

onMounted(() => {
  loadBooks()
  loadCategories()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>