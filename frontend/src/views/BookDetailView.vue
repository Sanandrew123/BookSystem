<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div v-if="loading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
    </div>
    
    <div v-else-if="book" class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Book Cover -->
      <div class="lg:col-span-1">
        <div class="sticky top-8">
          <div class="aspect-w-3 aspect-h-4">
            <div class="w-full h-96 bg-gradient-to-br from-accent-400 to-primary-500 rounded-lg flex items-center justify-center">
              <img v-if="book.coverImageUrl" :src="book.coverImageUrl" :alt="book.title" 
                   class="w-full h-full object-cover rounded-lg">
              <BookOpenIcon v-else class="h-24 w-24 text-white" />
            </div>
          </div>
          
          <div class="mt-6 space-y-3">
            <button v-if="book.availableCopies > 0" 
                    @click="borrowBook"
                    class="w-full btn-primary">
              <BookOpenIcon class="h-5 w-5 mr-2" />
              Borrow This Book
            </button>
            <button v-else disabled class="w-full btn-outline opacity-50 cursor-not-allowed">
              <ExclamationTriangleIcon class="h-5 w-5 mr-2" />
              Currently Unavailable
            </button>
            
            <div class="flex space-x-2">
              <button @click="addToWishlist" class="flex-1 btn-outline">
                <HeartIcon class="h-5 w-5 mr-2" />
                Wishlist
              </button>
              <button @click="shareBook" class="flex-1 btn-outline">
                <ShareIcon class="h-5 w-5 mr-2" />
                Share
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Book Details -->
      <div class="lg:col-span-2 space-y-6">
        <div>
          <h1 class="text-3xl font-serif font-bold text-gray-900">{{ book.title }}</h1>
          <p class="text-xl text-gray-600 mt-2">{{ book.author }}</p>
          
          <div class="flex items-center mt-4 space-x-4">
            <div class="flex items-center">
              <StarIcon v-for="i in 5" :key="i" 
                       class="h-5 w-5" 
                       :class="i <= book.rating ? 'text-yellow-400' : 'text-gray-300'" />
            </div>
            <span class="text-gray-600">({{ book.reviewCount }} reviews)</span>
          </div>
        </div>
        
        <div class="grid grid-cols-2 gap-6">
          <div class="space-y-3">
            <div>
              <dt class="text-sm font-medium text-gray-500">Publisher</dt>
              <dd class="text-sm text-gray-900">{{ book.publisher }}</dd>
            </div>
            <div>
              <dt class="text-sm font-medium text-gray-500">Publication Date</dt>
              <dd class="text-sm text-gray-900">{{ formatDate(book.publicationDate) }}</dd>
            </div>
            <div>
              <dt class="text-sm font-medium text-gray-500">Category</dt>
              <dd class="text-sm text-gray-900">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800">
                  {{ book.category }}
                </span>
              </dd>
            </div>
          </div>
          
          <div class="space-y-3">
            <div>
              <dt class="text-sm font-medium text-gray-500">ISBN</dt>
              <dd class="text-sm text-gray-900 font-mono">{{ book.isbn }}</dd>
            </div>
            <div>
              <dt class="text-sm font-medium text-gray-500">Availability</dt>
              <dd class="text-sm">
                <span v-if="book.availableCopies > 0" class="text-success-600 font-medium">
                  {{ book.availableCopies }} of {{ book.totalCopies }} available
                </span>
                <span v-else class="text-danger-600 font-medium">Out of stock</span>
              </dd>
            </div>
            <div>
              <dt class="text-sm font-medium text-gray-500">Status</dt>
              <dd class="text-sm">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                      :class="getStatusColor(book.status)">
                  {{ book.status }}
                </span>
              </dd>
            </div>
          </div>
        </div>
        
        <div v-if="book.description">
          <h3 class="text-lg font-serif font-semibold text-gray-900 mb-3">Description</h3>
          <p class="text-gray-700 leading-relaxed">{{ book.description }}</p>
        </div>
        
        <!-- Similar Books -->
        <div v-if="similarBooks.length > 0">
          <h3 class="text-lg font-serif font-semibold text-gray-900 mb-4">Similar Books</h3>
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div v-for="similarBook in similarBooks" :key="similarBook.id" 
                 class="cursor-pointer group" 
                 @click="$router.push(`/books/${similarBook.id}`)">
              <div class="w-full h-32 bg-gradient-to-br from-secondary-400 to-primary-500 rounded-md flex items-center justify-center group-hover:shadow-md transition-shadow">
                <img v-if="similarBook.coverImageUrl" :src="similarBook.coverImageUrl" :alt="similarBook.title" 
                     class="w-full h-full object-cover rounded-md">
                <BookOpenIcon v-else class="h-12 w-12 text-white" />
              </div>
              <p class="text-sm font-medium text-gray-900 mt-2 truncate">{{ similarBook.title }}</p>
              <p class="text-xs text-gray-500 truncate">{{ similarBook.author }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="text-center py-12">
      <BookOpenIcon class="mx-auto h-12 w-12 text-gray-400" />
      <h3 class="mt-2 text-sm font-medium text-gray-900">Book not found</h3>
      <p class="mt-1 text-sm text-gray-500">The book you're looking for doesn't exist.</p>
      <router-link to="/books" class="mt-4 btn-primary inline-flex items-center">
        <ArrowLeftIcon class="h-5 w-5 mr-2" />
        Back to Books
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import type { Book } from '../types'
import {
  BookOpenIcon,
  StarIcon,
  ExclamationTriangleIcon,
  HeartIcon,
  ShareIcon,
  ArrowLeftIcon
} from '@heroicons/vue/24/outline'

const route = useRoute()
const router = useRouter()

const book = ref<Book | null>(null)
const similarBooks = ref<Book[]>([])
const loading = ref(true)

const loadBook = async () => {
  try {
    loading.value = true
    const bookId = route.params.id
    const response = await axios.get(`/api/books/${bookId}`)
    book.value = response.data
    
    if (book.value) {
      loadSimilarBooks()
    }
  } catch (error) {
    console.error('Failed to load book:', error)
  } finally {
    loading.value = false
  }
}

const loadSimilarBooks = async () => {
  if (!book.value) return
  
  try {
    const response = await axios.get('/api/books', {
      params: {
        category: book.value.category,
        size: 8,
        page: 0
      }
    })
    
    // Filter out the current book and limit to 4 similar books
    similarBooks.value = response.data.content
      .filter((b: Book) => b.id !== book.value!.id)
      .slice(0, 4)
  } catch (error) {
    console.error('Failed to load similar books:', error)
  }
}

const borrowBook = async () => {
  if (!book.value) return
  
  try {
    const dueDate = new Date()
    dueDate.setDate(dueDate.getDate() + 14) // 14 days from now
    
    await axios.post('/api/borrows/borrow', {
      bookId: book.value.id,
      dueDate: dueDate.toISOString().split('T')[0]
    })
    
    // Refresh book data to update availability
    loadBook()
    
    alert(`"${book.value.title}" has been successfully borrowed!`)
  } catch (error: any) {
    alert(error.response?.data?.message || 'Failed to borrow book')
  }
}

const addToWishlist = () => {
  // TODO: Implement wishlist functionality
  alert('Wishlist functionality coming soon!')
}

const shareBook = () => {
  if (navigator.share) {
    navigator.share({
      title: book.value?.title,
      text: `Check out "${book.value?.title}" by ${book.value?.author}`,
      url: window.location.href
    })
  } else {
    // Fallback: copy to clipboard
    navigator.clipboard.writeText(window.location.href)
    alert('Book link copied to clipboard!')
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'AVAILABLE':
      return 'bg-success-100 text-success-800'
    case 'OUT_OF_STOCK':
      return 'bg-danger-100 text-danger-800'
    case 'DISCONTINUED':
      return 'bg-gray-100 text-gray-800'
    default:
      return 'bg-secondary-100 text-secondary-800'
  }
}

onMounted(() => {
  loadBook()
})
</script>