<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-serif font-bold text-gray-900">Book Management</h1>
        <p class="mt-1 text-sm text-gray-600">Manage library books and inventory</p>
      </div>
      <button @click="showCreateModal = true" class="btn-primary">
        <PlusIcon class="w-5 h-5 mr-2" />
        Add Book
      </button>
    </div>

    <!-- Filters -->
    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
        <div class="md:col-span-2">
          <label class="form-label">Search</label>
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <input v-model="filters.search" type="text" class="input-field pl-10" 
                   placeholder="Search books, authors, ISBN..." @input="debouncedSearch">
          </div>
        </div>
        <div>
          <label class="form-label">Category</label>
          <select v-model="filters.category" @change="loadBooks" class="input-field">
            <option value="">All Categories</option>
            <option v-for="category in categories" :key="category" :value="category">
              {{ category }}
            </option>
          </select>
        </div>
        <div>
          <label class="form-label">Status</label>
          <select v-model="filters.status" @change="loadBooks" class="input-field">
            <option value="">All Status</option>
            <option value="AVAILABLE">Available</option>
            <option value="OUT_OF_STOCK">Out of Stock</option>
            <option value="DISCONTINUED">Discontinued</option>
          </select>
        </div>
        <div class="flex items-end">
          <button @click="clearFilters" class="btn-outline w-full">
            Clear Filters
          </button>
        </div>
      </div>
    </div>

    <!-- Books Table -->
    <div class="card p-0">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Book
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Author & Publisher
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Category
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Availability
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Rating
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="loading">
              <td colspan="6" class="px-6 py-12 text-center">
                <div class="flex justify-center">
                  <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
                </div>
              </td>
            </tr>
            
            <tr v-else-if="!books.length">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500">
                No books found
              </td>
            </tr>
            
            <tr v-else v-for="book in books" :key="book.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="w-12 h-16 bg-gradient-to-br from-accent-400 to-primary-500 rounded-lg flex items-center justify-center flex-shrink-0">
                    <img v-if="book.coverImageUrl" :src="book.coverImageUrl" :alt="book.title" 
                         class="w-full h-full object-cover rounded-lg">
                    <BookOpenIcon v-else class="h-6 w-6 text-white" />
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900">{{ book.title }}</div>
                    <div class="text-sm text-gray-500">ISBN: {{ book.isbn }}</div>
                    <div class="text-xs text-gray-400">{{ formatDate(book.publicationDate) }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ book.author }}</div>
                <div class="text-sm text-gray-500">{{ book.publisher }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800">
                  {{ book.category }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ book.availableCopies }} / {{ book.totalCopies }}</div>
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                      :class="getAvailabilityColor(book)">
                  {{ getAvailabilityText(book) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="flex items-center">
                    <StarIcon v-for="i in 5" :key="i" 
                             class="h-4 w-4" 
                             :class="i <= book.rating ? 'text-yellow-400' : 'text-gray-300'" />
                  </div>
                  <span class="ml-2 text-sm text-gray-500">({{ book.reviewCount }})</span>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                <button @click="editBook(book)" class="text-primary-600 hover:text-primary-900">
                  Edit
                </button>
                <button @click="updateCopies(book)" class="text-accent-600 hover:text-accent-900">
                  Copies
                </button>
                <button @click="deleteBook(book)" class="text-danger-600 hover:text-danger-900">
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- Pagination -->
      <div class="bg-white px-4 py-3 border-t border-gray-200 sm:px-6">
        <div class="flex items-center justify-between">
          <div class="text-sm text-gray-700">
            Showing {{ ((currentPage - 1) * pageSize) + 1 }} to {{ Math.min(currentPage * pageSize, totalElements) }} 
            of {{ totalElements }} results
          </div>
          <div class="flex space-x-2">
            <button @click="goToPage(currentPage - 1)" :disabled="currentPage <= 1" 
                    class="btn-outline px-3 py-1 disabled:opacity-50">
              Previous
            </button>
            <span class="px-3 py-1 text-sm text-gray-700">
              Page {{ currentPage }} of {{ totalPages }}
            </span>
            <button @click="goToPage(currentPage + 1)" :disabled="currentPage >= totalPages" 
                    class="btn-outline px-3 py-1 disabled:opacity-50">
              Next
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create/Edit Book Modal -->
    <div v-if="showCreateModal || showEditModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" @click="closeModals"></div>
        
        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-2xl sm:w-full sm:p-6">
          <div>
            <h3 class="text-lg leading-6 font-serif font-semibold text-gray-900 mb-4">
              {{ showCreateModal ? 'Add New Book' : 'Edit Book' }}
            </h3>
            
            <form @submit.prevent="saveBook" class="space-y-4">
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="form-label">Title *</label>
                  <input v-model="bookForm.title" type="text" required class="input-field">
                </div>
                <div>
                  <label class="form-label">Author *</label>
                  <input v-model="bookForm.author" type="text" required class="input-field">
                </div>
              </div>
              
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="form-label">ISBN *</label>
                  <input v-model="bookForm.isbn" type="text" required class="input-field">
                </div>
                <div>
                  <label class="form-label">Publisher *</label>
                  <input v-model="bookForm.publisher" type="text" required class="input-field">
                </div>
              </div>
              
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="form-label">Publication Date *</label>
                  <input v-model="bookForm.publicationDate" type="date" required class="input-field">
                </div>
                <div>
                  <label class="form-label">Category *</label>
                  <input v-model="bookForm.category" type="text" required class="input-field" 
                         list="categories">
                  <datalist id="categories">
                    <option v-for="category in categories" :key="category" :value="category">
                  </datalist>
                </div>
              </div>
              
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="form-label">Total Copies *</label>
                  <input v-model.number="bookForm.totalCopies" type="number" min="1" required class="input-field">
                </div>
                <div>
                  <label class="form-label">Cover Image URL</label>
                  <input v-model="bookForm.coverImageUrl" type="url" class="input-field">
                </div>
              </div>
              
              <div>
                <label class="form-label">Description</label>
                <textarea v-model="bookForm.description" class="input-field" rows="3"></textarea>
              </div>
              
              <div class="flex justify-end space-x-3 pt-4">
                <button type="button" @click="closeModals" class="btn-outline">
                  Cancel
                </button>
                <button type="submit" :disabled="saving" class="btn-primary">
                  {{ saving ? 'Saving...' : (showCreateModal ? 'Create Book' : 'Update Book') }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Update Copies Modal -->
    <div v-if="showCopiesModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" @click="closeCopiesModal"></div>
        
        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-md sm:w-full sm:p-6">
          <div>
            <h3 class="text-lg leading-6 font-serif font-semibold text-gray-900 mb-4">
              Update Book Copies
            </h3>
            
            <div class="mb-4">
              <p class="text-sm text-gray-600 mb-2">
                <strong>{{ bookForCopies?.title }}</strong>
              </p>
              <p class="text-xs text-gray-500">
                Current: {{ bookForCopies?.availableCopies }} available / {{ bookForCopies?.totalCopies }} total
              </p>
            </div>
            
            <form @submit.prevent="saveCopies" class="space-y-4">
              <div>
                <label class="form-label">Total Copies</label>
                <input v-model.number="newTotalCopies" type="number" min="0" required class="input-field">
                <p class="text-xs text-gray-500 mt-1">
                  Available copies will be adjusted automatically
                </p>
              </div>
              
              <div class="flex justify-end space-x-3">
                <button type="button" @click="closeCopiesModal" class="btn-outline">
                  Cancel
                </button>
                <button type="submit" :disabled="saving" class="btn-primary">
                  {{ saving ? 'Updating...' : 'Update Copies' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import type { Book } from '../../types'
import { 
  PlusIcon, 
  MagnifyingGlassIcon, 
  BookOpenIcon, 
  StarIcon 
} from '@heroicons/vue/24/outline'

const books = ref<Book[]>([])
const categories = ref<string[]>([])
const loading = ref(true)
const saving = ref(false)

const showCreateModal = ref(false)
const showEditModal = ref(false)
const showCopiesModal = ref(false)

const currentPage = ref(1)
const pageSize = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)

const bookForCopies = ref<Book | null>(null)
const newTotalCopies = ref(0)

const filters = reactive({
  search: '',
  category: '',
  status: ''
})

const bookForm = reactive({
  id: null,
  title: '',
  author: '',
  isbn: '',
  publisher: '',
  publicationDate: '',
  category: '',
  description: '',
  totalCopies: 1,
  coverImageUrl: ''
})

let searchTimeout: NodeJS.Timeout

const debouncedSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    currentPage.value = 1
    loadBooks()
  }, 500)
}

const loadBooks = async () => {
  try {
    loading.value = true
    const params = new URLSearchParams({
      page: (currentPage.value - 1).toString(),
      size: pageSize.value.toString()
    })
    
    if (filters.search) params.append('search', filters.search)
    if (filters.category) params.append('category', filters.category)
    if (filters.status) params.append('status', filters.status)
    
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

const clearFilters = () => {
  filters.search = ''
  filters.category = ''
  filters.status = ''
  currentPage.value = 1
  loadBooks()
}

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    loadBooks()
  }
}

const editBook = (book: Book) => {
  Object.assign(bookForm, {
    ...book,
    publicationDate: book.publicationDate.split('T')[0] // Format for date input
  })
  showEditModal.value = true
}

const saveBook = async () => {
  try {
    saving.value = true
    
    if (showCreateModal.value) {
      await axios.post('/api/books', bookForm)
    } else {
      await axios.put(`/api/books/${bookForm.id}`, bookForm)
    }
    
    closeModals()
    loadBooks()
    loadCategories() // Refresh categories in case a new one was added
  } catch (error: any) {
    alert(error.response?.data?.message || 'Failed to save book')
  } finally {
    saving.value = false
  }
}

const updateCopies = (book: Book) => {
  bookForCopies.value = book
  newTotalCopies.value = book.totalCopies
  showCopiesModal.value = true
}

const saveCopies = async () => {
  try {
    saving.value = true
    await axios.put(`/api/books/${bookForCopies.value?.id}/copies`, {
      totalCopies: newTotalCopies.value
    })
    
    closeCopiesModal()
    loadBooks()
  } catch (error: any) {
    alert(error.response?.data?.message || 'Failed to update copies')
  } finally {
    saving.value = false
  }
}

const deleteBook = async (book: Book) => {
  if (confirm(`Are you sure you want to delete "${book.title}"?`)) {
    try {
      await axios.delete(`/api/books/${book.id}`)
      loadBooks()
    } catch (error: any) {
      alert(error.response?.data?.message || 'Failed to delete book')
    }
  }
}

const closeModals = () => {
  showCreateModal.value = false
  showEditModal.value = false
  Object.assign(bookForm, {
    id: null,
    title: '',
    author: '',
    isbn: '',
    publisher: '',
    publicationDate: '',
    category: '',
    description: '',
    totalCopies: 1,
    coverImageUrl: ''
  })
}

const closeCopiesModal = () => {
  showCopiesModal.value = false
  bookForCopies.value = null
  newTotalCopies.value = 0
}

const formatDate = (dateString: string) => {
  return new Date(dateString).getFullYear()
}

const getAvailabilityColor = (book: Book) => {
  if (book.availableCopies === 0) {
    return 'bg-danger-100 text-danger-800'
  } else if (book.availableCopies <= book.totalCopies * 0.2) {
    return 'bg-orange-100 text-orange-800'
  }
  return 'bg-success-100 text-success-800'
}

const getAvailabilityText = (book: Book) => {
  if (book.availableCopies === 0) {
    return 'Out of Stock'
  } else if (book.availableCopies <= book.totalCopies * 0.2) {
    return 'Low Stock'
  }
  return 'Available'
}

onMounted(() => {
  loadBooks()
  loadCategories()
})
</script>