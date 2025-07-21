<template>
  <div class="space-y-6">
    <!-- Header -->
    <div>
      <h1 class="text-2xl font-serif font-bold text-gray-900">Admin Dashboard</h1>
      <p class="mt-1 text-sm text-gray-600">Monitor and manage your library system</p>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="card">
        <div class="flex items-center">
          <div class="p-3 rounded-lg bg-primary-100">
            <BookOpenIcon class="h-6 w-6 text-primary-600" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-500">Total Books</p>
            <p class="text-2xl font-bold text-gray-900">{{ stats.totalBooks || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="card">
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

      <div class="card">
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

      <div class="card">
        <div class="flex items-center">
          <div class="p-3 rounded-lg bg-danger-100">
            <ExclamationTriangleIcon class="h-6 w-6 text-danger-600" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-500">Overdue Books</p>
            <p class="text-2xl font-bold text-gray-900">{{ stats.overdueBooks || 0 }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Recent Activity -->
      <div class="card">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-serif font-semibold text-gray-900">Recent Activity</h3>
          <router-link to="/admin/borrows" class="text-sm text-primary-600 hover:text-primary-500">
            View all
          </router-link>
        </div>
        
        <div v-if="loading" class="flex justify-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
        </div>
        
        <div v-else-if="stats.recentActivity?.length" class="space-y-4">
          <div v-for="record in stats.recentActivity" :key="record.id" 
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
        
        <div v-else class="text-center py-8 text-gray-500">
          No recent activity
        </div>
      </div>

      <!-- Overdue Books -->
      <div class="card">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-serif font-semibold text-gray-900">
            Overdue Books
            <span v-if="stats.overdueRecords?.length" 
                  class="ml-2 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-danger-100 text-danger-800">
              {{ stats.overdueRecords.length }}
            </span>
          </h3>
          <button @click="updateOverdueStatus" 
                  :disabled="updatingOverdue"
                  class="text-sm text-primary-600 hover:text-primary-500 disabled:opacity-50">
            {{ updatingOverdue ? 'Updating...' : 'Update Status' }}
          </button>
        </div>
        
        <div v-if="stats.overdueRecords?.length" class="space-y-4">
          <div v-for="record in stats.overdueRecords.slice(0, 5)" :key="record.id" 
               class="flex items-center space-x-4 p-3 rounded-lg bg-danger-50 border border-danger-200">
            <div class="w-10 h-10 rounded-full bg-danger-100 flex items-center justify-center">
              <span class="text-sm font-medium text-danger-600">
                {{ record.user.firstName.charAt(0) }}{{ record.user.lastName.charAt(0) }}
              </span>
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-900">
                {{ record.user.firstName }} {{ record.user.lastName }}
              </p>
              <p class="text-sm text-gray-600 truncate">{{ record.book.title }}</p>
              <p class="text-xs text-danger-600">
                Due: {{ formatDate(record.dueDate) }} ({{ getDaysOverdue(record.dueDate) }} days overdue)
              </p>
            </div>
            <div class="text-right">
              <button @click="markAsReturned(record.id)" 
                      class="text-xs text-primary-600 hover:text-primary-500">
                Mark Returned
              </button>
            </div>
          </div>
        </div>
        
        <div v-else class="text-center py-8 text-gray-500">
          No overdue books
        </div>
      </div>
    </div>

    <!-- Popular Books -->
    <div class="card">
      <h3 class="text-lg font-serif font-semibold text-gray-900 mb-4">Most Popular Books</h3>
      
      <div v-if="stats.popularBooks?.length" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="book in stats.popularBooks" :key="book.id" 
             class="flex items-center space-x-4 p-4 rounded-lg border border-gray-200 hover:shadow-md transition-shadow cursor-pointer"
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
              <span class="ml-2 text-xs text-gray-500">({{ book.reviewCount }} reviews)</span>
            </div>
            <p class="text-xs text-primary-600 mt-1">
              {{ book.availableCopies }} / {{ book.totalCopies }} available
            </p>
          </div>
        </div>
      </div>
      
      <div v-else class="text-center py-8 text-gray-500">
        No popular books data available
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="card">
      <h3 class="text-lg font-serif font-semibold text-gray-900 mb-4">Quick Actions</h3>
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <router-link to="/admin/books" class="btn-primary text-center py-3">
          Add New Book
        </router-link>
        
        <router-link v-if="isAdmin" to="/admin/users" class="btn-secondary text-center py-3">
          Manage Users
        </router-link>
        
        <router-link to="/admin/borrows" class="btn-outline text-center py-3">
          View All Borrows
        </router-link>
        
        <button @click="exportReport" class="btn-outline py-3">
          Export Report
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import axios from 'axios'
import {
  BookOpenIcon,
  UserGroupIcon,
  ClockIcon,
  ExclamationTriangleIcon,
  StarIcon
} from '@heroicons/vue/24/outline'

const authStore = useAuthStore()
const stats = ref({})
const loading = ref(true)
const updatingOverdue = ref(false)

const isAdmin = computed(() => authStore.isAdmin)

const fetchStats = async () => {
  try {
    loading.value = true
    const response = await axios.get('/api/dashboard/stats')
    stats.value = response.data
  } catch (error) {
    console.error('Failed to fetch admin stats:', error)
  } finally {
    loading.value = false
  }
}

const updateOverdueStatus = async () => {
  try {
    updatingOverdue.value = true
    await axios.post('/api/borrows/update-overdue-status')
    await fetchStats() // Refresh data
  } catch (error) {
    console.error('Failed to update overdue status:', error)
  } finally {
    updatingOverdue.value = false
  }
}

const markAsReturned = async (borrowRecordId: number) => {
  try {
    await axios.post(`/api/borrows/${borrowRecordId}/return`)
    await fetchStats() // Refresh data
  } catch (error) {
    console.error('Failed to mark as returned:', error)
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

const getDaysOverdue = (dueDateString: string) => {
  const dueDate = new Date(dueDateString)
  const today = new Date()
  const diffTime = today.getTime() - dueDate.getTime()
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
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

const exportReport = () => {
  // TODO: Implement report export functionality
  console.log('Export report functionality to be implemented')
}

onMounted(() => {
  fetchStats()
})
</script>