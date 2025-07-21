<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto">
    <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
      <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" @click="$emit('close')"></div>
      
      <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-2xl sm:w-full sm:p-6">
        <div class="absolute top-0 right-0 pt-4 pr-4">
          <button @click="$emit('close')" class="bg-white rounded-md text-gray-400 hover:text-gray-600 focus:outline-none">
            <XMarkIcon class="h-6 w-6" />
          </button>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <!-- Book Cover -->
          <div class="md:col-span-1">
            <div class="aspect-w-3 aspect-h-4">
              <div class="w-full h-80 bg-gradient-to-br from-accent-400 to-primary-500 rounded-lg flex items-center justify-center">
                <img v-if="book.coverImageUrl" :src="book.coverImageUrl" :alt="book.title" 
                     class="w-full h-full object-cover rounded-lg">
                <BookOpenIcon v-else class="h-20 w-20 text-white" />
              </div>
            </div>
          </div>
          
          <!-- Book Details -->
          <div class="md:col-span-2 space-y-4">
            <div>
              <h2 class="text-2xl font-serif font-bold text-gray-900">{{ book.title }}</h2>
              <p class="text-lg text-gray-600 mt-1">{{ book.author }}</p>
            </div>
            
            <div class="flex items-center space-x-4">
              <div class="flex items-center">
                <StarIcon v-for="i in 5" :key="i" 
                         class="h-5 w-5" 
                         :class="i <= book.rating ? 'text-yellow-400' : 'text-gray-300'" />
              </div>
              <span class="text-sm text-gray-600">({{ book.reviewCount }} reviews)</span>
            </div>
            
            <div class="space-y-2 text-sm">
              <div class="flex">
                <span class="font-medium text-gray-700 w-24">Publisher:</span>
                <span class="text-gray-600">{{ book.publisher }}</span>
              </div>
              <div class="flex">
                <span class="font-medium text-gray-700 w-24">Published:</span>
                <span class="text-gray-600">{{ formatDate(book.publicationDate) }}</span>
              </div>
              <div class="flex">
                <span class="font-medium text-gray-700 w-24">Category:</span>
                <span class="text-gray-600">{{ book.category }}</span>
              </div>
              <div class="flex">
                <span class="font-medium text-gray-700 w-24">ISBN:</span>
                <span class="text-gray-600">{{ book.isbn }}</span>
              </div>
              <div class="flex">
                <span class="font-medium text-gray-700 w-24">Availability:</span>
                <span v-if="book.availableCopies > 0" class="text-success-600 font-medium">
                  {{ book.availableCopies }} of {{ book.totalCopies }} available
                </span>
                <span v-else class="text-danger-600 font-medium">Out of stock</span>
              </div>
            </div>
            
            <div v-if="book.description" class="space-y-2">
              <h3 class="font-medium text-gray-900">Description</h3>
              <p class="text-gray-600 text-sm leading-relaxed">{{ book.description }}</p>
            </div>
            
            <div class="flex items-center space-x-3 pt-4">
              <button v-if="book.availableCopies > 0" 
                      @click="$emit('borrow', book)"
                      class="btn-primary flex-1">
                <BookOpenIcon class="h-5 w-5 mr-2" />
                Borrow This Book
              </button>
              <button v-else disabled class="btn-outline flex-1 opacity-50 cursor-not-allowed">
                <ExclamationTriangleIcon class="h-5 w-5 mr-2" />
                Currently Unavailable
              </button>
              
              <button @click="addToWishlist" class="btn-outline px-4">
                <HeartIcon class="h-5 w-5" />
              </button>
              
              <button @click="shareBook" class="btn-outline px-4">
                <ShareIcon class="h-5 w-5" />
              </button>
            </div>
            
            <!-- Similar Books Section -->
            <div class="pt-6 border-t border-gray-200">
              <h3 class="font-medium text-gray-900 mb-3">Similar Books</h3>
              <div class="grid grid-cols-3 gap-3">
                <div v-for="similarBook in similarBooks" :key="similarBook.id" 
                     class="cursor-pointer group" 
                     @click="selectSimilarBook(similarBook)">
                  <div class="w-full h-20 bg-gradient-to-br from-secondary-400 to-primary-500 rounded-md flex items-center justify-center group-hover:shadow-md transition-shadow">
                    <img v-if="similarBook.coverImageUrl" :src="similarBook.coverImageUrl" :alt="similarBook.title" 
                         class="w-full h-full object-cover rounded-md">
                    <BookOpenIcon v-else class="h-8 w-8 text-white" />
                  </div>
                  <p class="text-xs text-gray-600 mt-1 truncate">{{ similarBook.title }}</p>
                  <p class="text-xs text-gray-500 truncate">{{ similarBook.author }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import axios from 'axios'
import type { Book } from '../types'
import {
  XMarkIcon,
  BookOpenIcon,
  StarIcon,
  ExclamationTriangleIcon,
  HeartIcon,
  ShareIcon
} from '@heroicons/vue/24/outline'

interface Props {
  book: Book
  isOpen: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  close: []
  borrow: [book: Book]
}>()

const similarBooks = ref<Book[]>([])

const loadSimilarBooks = async () => {
  try {
    const response = await axios.get('/api/books', {
      params: {
        category: props.book.category,
        size: 6,
        page: 0
      }
    })
    
    // Filter out the current book and limit to 3 similar books
    similarBooks.value = response.data.content
      .filter((b: Book) => b.id !== props.book.id)
      .slice(0, 3)
  } catch (error) {
    console.error('Failed to load similar books:', error)
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

const addToWishlist = () => {
  // TODO: Implement wishlist functionality
  alert('Wishlist functionality coming soon!')
}

const shareBook = () => {
  if (navigator.share) {
    navigator.share({
      title: props.book.title,
      text: `Check out "${props.book.title}" by ${props.book.author}`,
      url: window.location.href
    })
  } else {
    // Fallback: copy to clipboard
    navigator.clipboard.writeText(window.location.href)
    alert('Book link copied to clipboard!')
  }
}

const selectSimilarBook = (book: Book) => {
  emit('close')
  // You might want to emit a different event or handle this differently
  // For now, we'll just close the modal
}

watch(() => props.isOpen, (isOpen) => {
  if (isOpen && props.book) {
    loadSimilarBooks()
  }
})

onMounted(() => {
  if (props.isOpen && props.book) {
    loadSimilarBooks()
  }
})
</script>