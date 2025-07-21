<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-serif font-bold text-gray-900">
        Welcome back, {{ user?.firstName }}!
      </h1>
      <p class="mt-2 text-gray-600">
        Here's what's happening in your library today.
      </p>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <div class="card">
        <div class="flex items-center">
          <div class="p-3 rounded-lg bg-primary-100">
            <BookOpenIcon class="h-6 w-6 text-primary-600" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-500">Available Books</p>
            <p class="text-2xl font-bold text-gray-900">{{ stats.availableBooks || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="card" v-if="isLibrarian">
        <div class="flex items-center">
          <div class="p-3 rounded-lg bg-accent-100">
            <UserGroupIcon class="h-6 w-6 text-accent-600" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-500">Total Users</p>
            <p class="text-2xl font-bold text-gray-900">{{ stats.totalUsers || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="card" v-if="isLibrarian">
        <div class="flex items-center">
          <div class="p-3 rounded-lg bg-success-100">
            <ClockIcon class="h-6 w-6 text-success-600" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-500">Active Borrows</p>
            <p class="text-2xl font-bold text-gray-900">{{ stats.activeBorrows || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="card" v-if="isLibrarian">
        <div class="flex items-center">
          <div class="p-3 rounded-lg bg-danger-100">
            <ExclamationTriangleIcon class="h-6 w-6 text-danger-600" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-500">Overdue</p>
            <p class="text-2xl font-bold text-gray-900">{{ stats.overdueBooks || 0 }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
      <!-- Top Rated Books -->
      <div class="card">
        <h3 class="text-lg font-serif font-semibold text-gray-900 mb-4">
          Top Rated Books
        </h3>
        <div class="space-y-4">
          <div v-for="book in stats.topRatedBooks?.slice(0, 5)" :key="book.id" 
               class="flex items-center space-x-4 p-3 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer"
               @click="$router.push(`/books/${book.id}`)">
            <div class="w-12 h-16 bg-gradient-to-br from-accent-400 to-primary-500 rounded-lg flex items-center justify-center">
              <BookOpenIcon class="h-6 w-6 text-white" />
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-900 truncate">{{ book.title }}</p>
              <p class="text-sm text-gray-500 truncate">{{ book.author }}</p>
              <div class="flex items-center mt-1">
                <div class="flex items-center">
                  <StarIcon v-for="i in 5" :key="i" 
                           class="h-4 w-4" 
                           :class="i <= book.rating ? 'text-yellow-400' : 'text-gray-300'" />
                </div>
                <span class="ml-2 text-sm text-gray-500">({{ book.reviewCount }})</span>
              </div>
            </div>
            <div class="text-right">
              <p class="text-sm font-medium text-primary-600">
                {{ book.availableCopies }} / {{ book.totalCopies }}
              </p>
              <p class="text-xs text-gray-500">Available</p>
            </div>
          </div>
        </div>
        <div class="mt-4 pt-4 border-t border-gray-200">
          <router-link to="/books" class="text-sm font-medium text-primary-600 hover:text-primary-500">
            View all books →
          </router-link>
        </div>
      </div>

      <!-- Recent Activity (Librarian/Admin only) -->
      <div class="card" v-if="isLibrarian">
        <h3 class="text-lg font-serif font-semibold text-gray-900 mb-4">
          Recent Activity
        </h3>
        <div class="space-y-4">
          <div v-for="record in stats.recentActivity?.slice(0, 5)" :key="record.id" 
               class="flex items-center space-x-4 p-3 rounded-lg hover:bg-gray-50 transition-colors">
            <div class="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center">
              <span class="text-sm font-medium text-primary-600">
                {{ record.user.firstName.charAt(0) }}{{ record.user.lastName.charAt(0) }}
              </span>
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-900">
                {{ record.user.firstName }} {{ record.user.lastName }}
              </p>
              <p class="text-sm text-gray-500 truncate">{{ record.book.title }}</p>
              <p class="text-xs text-gray-400">{{ formatDate(record.createdAt) }}</p>
            </div>
            <div class="text-right">
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                    :class="getStatusColor(record.status)">
                {{ record.status }}
              </span>
            </div>
          </div>
        </div>
        <div class="mt-4 pt-4 border-t border-gray-200">
          <router-link to="/admin/borrows" class="text-sm font-medium text-primary-600 hover:text-primary-500">
            View all activity →
          </router-link>
        </div>
      </div>

      <!-- My Recent Books (Student view) -->
      <div class="card" v-if="!isLibrarian">
        <h3 class="text-lg font-serif font-semibold text-gray-900 mb-4">
          Recently Added Books
        </h3>
        <div class="space-y-4">
          <div v-for="book in stats.recentBooks?.slice(0, 5)" :key="book.id" 
               class="flex items-center space-x-4 p-3 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer"
               @click="$router.push(`/books/${book.id}`)">
            <div class="w-12 h-16 bg-gradient-to-br from-secondary-400 to-primary-500 rounded-lg flex items-center justify-center">
              <BookOpenIcon class="h-6 w-6 text-white" />
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-900 truncate">{{ book.title }}</p>
              <p class="text-sm text-gray-500 truncate">{{ book.author }}</p>
              <p class="text-xs text-gray-400">{{ book.category }}</p>
            </div>
            <div class="text-right">
              <span v-if="book.availableCopies > 0" 
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-success-100 text-success-800">
                Available
              </span>
              <span v-else 
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-danger-100 text-danger-800">
                Out of stock
              </span>
            </div>
          </div>
        </div>
        <div class="mt-4 pt-4 border-t border-gray-200">
          <router-link to="/books" class="text-sm font-medium text-primary-600 hover:text-primary-500">
            Browse all books →
          </router-link>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="mt-8 card">
      <h3 class="text-lg font-serif font-semibold text-gray-900 mb-4">Quick Actions</h3>
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <router-link to="/books" class="btn-outline flex items-center justify-center space-x-2 py-3">
          <MagnifyingGlassIcon class="h-5 w-5" />
          <span>Browse Books</span>
        </router-link>
        
        <router-link to="/my-books" class="btn-outline flex items-center justify-center space-x-2 py-3">
          <BookmarkIcon class="h-5 w-5" />
          <span>My Books</span>
        </router-link>
        
        <router-link v-if="isLibrarian" to="/admin/books" class="btn-outline flex items-center justify-center space-x-2 py-3">
          <PlusIcon class="h-5 w-5" />
          <span>Add Book</span>
        </router-link>
        
        <router-link v-if="isAdmin" to="/admin/users" class="btn-outline flex items-center justify-center space-x-2 py-3">
          <UsersIcon class="h-5 w-5" />
          <span>Manage Users</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import axios from 'axios'
import {
  BookOpenIcon,
  UserGroupIcon,
  ClockIcon,
  ExclamationTriangleIcon,
  StarIcon,
  MagnifyingGlassIcon,
  BookmarkIcon,
  PlusIcon,
  UsersIcon
} from '@heroicons/vue/24/outline'

const authStore = useAuthStore()
const stats = ref({})

const user = computed(() => authStore.user)
const isLibrarian = computed(() => authStore.isLibrarian)
const isAdmin = computed(() => authStore.isAdmin)

const fetchStats = async () => {
  try {
    const endpoint = isLibrarian.value ? '/api/dashboard/stats' : '/api/dashboard/user-stats'
    const response = await axios.get(endpoint)
    stats.value = response.data
  } catch (error) {
    console.error('Failed to fetch dashboard stats:', error)
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
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

onMounted(() => {
  fetchStats()
})
</script>