<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-serif font-bold text-gray-900">Borrow Records</h1>
        <p class="mt-1 text-sm text-gray-600">Monitor all borrowing activity and manage returns</p>
      </div>
      <div class="flex space-x-3">
        <button @click="updateOverdueStatus" :disabled="updating" class="btn-outline">
          {{ updating ? 'Updating...' : 'Update Overdue Status' }}
        </button>
        <button @click="exportRecords" class="btn-primary">
          <DocumentArrowDownIcon class="w-5 h-5 mr-2" />
          Export
        </button>
      </div>
    </div>

    <!-- Summary Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div class="card">
        <div class="flex items-center">
          <div class="p-3 rounded-lg bg-primary-100">
            <BookOpenIcon class="h-6 w-6 text-primary-600" />
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
            <p class="text-sm font-medium text-gray-500">Overdue</p>
            <p class="text-2xl font-bold text-gray-900">{{ stats.overdue || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="flex items-center">
          <div class="p-3 rounded-lg bg-success-100">
            <CheckCircleIcon class="h-6 w-6 text-success-600" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-500">Returned Today</p>
            <p class="text-2xl font-bold text-gray-900">{{ stats.returnedToday || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="flex items-center">
          <div class="p-3 rounded-lg bg-orange-100">
            <ClockIcon class="h-6 w-6 text-orange-600" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-500">Due Soon</p>
            <p class="text-2xl font-bold text-gray-900">{{ stats.dueSoon || 0 }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
        <div class="md:col-span-2">
          <label class="form-label">Search</label>
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <input v-model="filters.search" type="text" class="input-field pl-10" 
                   placeholder="Search users, books..." @input="debouncedSearch">
          </div>
        </div>
        <div>
          <label class="form-label">Status</label>
          <select v-model="filters.status" @change="loadBorrowRecords" class="input-field">
            <option value="">All Status</option>
            <option value="BORROWED">Borrowed</option>
            <option value="RETURNED">Returned</option>
            <option value="OVERDUE">Overdue</option>
            <option value="LOST">Lost</option>
          </select>
        </div>
        <div>
          <label class="form-label">Date Range</label>
          <select v-model="filters.dateRange" @change="loadBorrowRecords" class="input-field">
            <option value="">All Time</option>
            <option value="today">Today</option>
            <option value="week">This Week</option>
            <option value="month">This Month</option>
          </select>
        </div>
        <div class="flex items-end">
          <button @click="clearFilters" class="btn-outline w-full">
            Clear Filters
          </button>
        </div>
      </div>
    </div>

    <!-- Borrow Records Table -->
    <div class="card p-0">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                User
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Book
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Dates
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Status
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Fine
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
            
            <tr v-else-if="!borrowRecords.length">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500">
                No borrow records found
              </td>
            </tr>
            
            <tr v-else v-for="record in borrowRecords" :key="record.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center">
                    <span class="text-sm font-medium text-primary-600">
                      {{ record.user.firstName.charAt(0) }}{{ record.user.lastName.charAt(0) }}
                    </span>
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900">
                      {{ record.user.firstName }} {{ record.user.lastName }}
                    </div>
                    <div class="text-sm text-gray-500">{{ record.user.email }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">{{ record.book.title }}</div>
                <div class="text-sm text-gray-500">{{ record.book.author }}</div>
                <div class="text-xs text-gray-400">{{ record.book.isbn }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <div>Borrowed: {{ formatDate(record.borrowDate) }}</div>
                <div :class="getDueDateColor(record.dueDate, record.status)">
                  Due: {{ formatDate(record.dueDate) }}
                  <span v-if="isOverdue(record.dueDate, record.status)" class="font-medium">
                    ({{ getDaysOverdue(record.dueDate) }} days overdue)
                  </span>
                </div>
                <div v-if="record.returnDate">Returned: {{ formatDate(record.returnDate) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                      :class="getStatusColor(record.status)">
                  {{ record.status }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                <span v-if="record.fineAmount > 0" class="text-danger-600 font-medium">
                  ${{ record.fineAmount.toFixed(2) }}
                </span>
                <span v-else class="text-gray-400">$0.00</span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                <button v-if="record.status === 'BORROWED'" 
                        @click="markAsReturned(record)"
                        class="text-success-600 hover:text-success-900">
                  Mark Returned
                </button>
                <button v-if="record.status === 'BORROWED' || record.status === 'OVERDUE'" 
                        @click="markAsLost(record)"
                        class="text-danger-600 hover:text-danger-900">
                  Mark Lost
                </button>
                <button @click="viewDetails(record)" class="text-primary-600 hover:text-primary-900">
                  Details
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

    <!-- Record Details Modal -->
    <div v-if="showDetailsModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" @click="closeDetailsModal"></div>
        
        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6">
          <div v-if="selectedRecord">
            <h3 class="text-lg leading-6 font-serif font-semibold text-gray-900 mb-4">
              Borrow Record Details
            </h3>
            
            <div class="space-y-4">
              <div>
                <h4 class="font-medium text-gray-900">User Information</h4>
                <p class="text-sm text-gray-600">{{ selectedRecord.user.firstName }} {{ selectedRecord.user.lastName }}</p>
                <p class="text-sm text-gray-500">{{ selectedRecord.user.email }}</p>
              </div>
              
              <div>
                <h4 class="font-medium text-gray-900">Book Information</h4>
                <p class="text-sm text-gray-600">{{ selectedRecord.book.title }}</p>
                <p class="text-sm text-gray-500">{{ selectedRecord.book.author }} • {{ selectedRecord.book.isbn }}</p>
              </div>
              
              <div>
                <h4 class="font-medium text-gray-900">Borrow Details</h4>
                <div class="text-sm space-y-1">
                  <p><span class="text-gray-500">Borrowed:</span> {{ formatDate(selectedRecord.borrowDate) }}</p>
                  <p><span class="text-gray-500">Due:</span> {{ formatDate(selectedRecord.dueDate) }}</p>
                  <p v-if="selectedRecord.returnDate"><span class="text-gray-500">Returned:</span> {{ formatDate(selectedRecord.returnDate) }}</p>
                  <p><span class="text-gray-500">Status:</span> 
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ml-2"
                          :class="getStatusColor(selectedRecord.status)">
                      {{ selectedRecord.status }}
                    </span>
                  </p>
                  <p v-if="selectedRecord.fineAmount > 0">
                    <span class="text-gray-500">Fine:</span> 
                    <span class="text-danger-600 font-medium">${{ selectedRecord.fineAmount.toFixed(2) }}</span>
                  </p>
                </div>
              </div>
              
              <div v-if="selectedRecord.notes">
                <h4 class="font-medium text-gray-900">Notes</h4>
                <p class="text-sm text-gray-600">{{ selectedRecord.notes }}</p>
              </div>
            </div>
            
            <div class="flex justify-end space-x-3 mt-6">
              <button @click="closeDetailsModal" class="btn-outline">
                Close
              </button>
              <button v-if="selectedRecord.status === 'BORROWED'" 
                      @click="markAsReturned(selectedRecord)"
                      class="btn-primary">
                Mark as Returned
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import type { BorrowRecord } from '../../types'
import {
  DocumentArrowDownIcon,
  BookOpenIcon,
  ExclamationTriangleIcon,
  CheckCircleIcon,
  ClockIcon,
  MagnifyingGlassIcon
} from '@heroicons/vue/24/outline'

const borrowRecords = ref<BorrowRecord[]>([])
const loading = ref(true)
const updating = ref(false)
const showDetailsModal = ref(false)
const selectedRecord = ref<BorrowRecord | null>(null)

const currentPage = ref(1)
const pageSize = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)

const stats = ref({
  activeBorrows: 0,
  overdue: 0,
  returnedToday: 0,
  dueSoon: 0
})

const filters = reactive({
  search: '',
  status: '',
  dateRange: ''
})

let searchTimeout: NodeJS.Timeout

const debouncedSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    currentPage.value = 1
    loadBorrowRecords()
  }, 500)
}

const loadBorrowRecords = async () => {
  try {
    loading.value = true
    const params = new URLSearchParams({
      page: (currentPage.value - 1).toString(),
      size: pageSize.value.toString()
    })
    
    if (filters.search) params.append('search', filters.search)
    if (filters.status) params.append('status', filters.status)
    
    const response = await axios.get(`/api/borrows?${params}`)
    borrowRecords.value = response.data.content
    totalElements.value = response.data.totalElements
    totalPages.value = response.data.totalPages
  } catch (error) {
    console.error('Failed to load borrow records:', error)
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    const response = await axios.get('/api/dashboard/stats')
    stats.value = response.data
  } catch (error) {
    console.error('Failed to load stats:', error)
  }
}

const clearFilters = () => {
  filters.search = ''
  filters.status = ''
  filters.dateRange = ''
  currentPage.value = 1
  loadBorrowRecords()
}

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    loadBorrowRecords()
  }
}

const updateOverdueStatus = async () => {
  try {
    updating.value = true
    await axios.post('/api/borrows/update-overdue-status')
    loadBorrowRecords()
    loadStats()
  } catch (error) {
    console.error('Failed to update overdue status:', error)
  } finally {
    updating.value = false
  }
}

const markAsReturned = async (record: BorrowRecord) => {
  try {
    await axios.post(`/api/borrows/${record.id}/return`)
    loadBorrowRecords()
    loadStats()
    closeDetailsModal()
  } catch (error: any) {
    alert(error.response?.data?.message || 'Failed to mark as returned')
  }
}

const markAsLost = async (record: BorrowRecord) => {
  const notes = prompt(`Mark "${record.book.title}" as lost. Add notes (optional):`)
  if (notes !== null) {
    try {
      await axios.post(`/api/borrows/${record.id}/mark-lost`, { notes })
      loadBorrowRecords()
      loadStats()
    } catch (error: any) {
      alert(error.response?.data?.message || 'Failed to mark as lost')
    }
  }
}

const viewDetails = (record: BorrowRecord) => {
  selectedRecord.value = record
  showDetailsModal.value = true
}

const closeDetailsModal = () => {
  showDetailsModal.value = false
  selectedRecord.value = null
}

const exportRecords = () => {
  // TODO: Implement export functionality
  alert('Export functionality coming soon!')
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
  
  return 'text-gray-500'
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
  loadBorrowRecords()
  loadStats()
})
</script>