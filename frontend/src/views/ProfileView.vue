<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-serif font-bold text-gray-900">Profile</h1>
      <p class="mt-2 text-gray-600">Manage your account information and preferences</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Profile Information -->
      <div class="lg:col-span-2 space-y-6">
        <div class="card">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-lg font-serif font-semibold text-gray-900">Personal Information</h2>
            <button @click="editMode = !editMode" class="btn-outline">
              {{ editMode ? 'Cancel' : 'Edit' }}
            </button>
          </div>
          
          <form v-if="editMode" @submit.prevent="updateProfile" class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="form-label">First Name</label>
                <input v-model="profileForm.firstName" type="text" required class="input-field">
              </div>
              <div>
                <label class="form-label">Last Name</label>
                <input v-model="profileForm.lastName" type="text" required class="input-field">
              </div>
            </div>
            
            <div>
              <label class="form-label">Email</label>
              <input v-model="profileForm.email" type="email" required class="input-field">
            </div>
            
            <div>
              <label class="form-label">Phone Number</label>
              <input v-model="profileForm.phoneNumber" type="tel" required class="input-field">
            </div>
            
            <div>
              <label class="form-label">New Password (Leave blank to keep current)</label>
              <input v-model="profileForm.password" type="password" class="input-field">
            </div>
            
            <div class="flex justify-end space-x-3">
              <button type="button" @click="editMode = false" class="btn-outline">
                Cancel
              </button>
              <button type="submit" :disabled="saving" class="btn-primary">
                {{ saving ? 'Saving...' : 'Save Changes' }}
              </button>
            </div>
          </form>
          
          <div v-else class="space-y-4">
            <div class="grid grid-cols-2 gap-6">
              <div>
                <dt class="text-sm font-medium text-gray-500">First Name</dt>
                <dd class="text-sm text-gray-900">{{ user?.firstName }}</dd>
              </div>
              <div>
                <dt class="text-sm font-medium text-gray-500">Last Name</dt>
                <dd class="text-sm text-gray-900">{{ user?.lastName }}</dd>
              </div>
            </div>
            
            <div>
              <dt class="text-sm font-medium text-gray-500">Username</dt>
              <dd class="text-sm text-gray-900">{{ user?.username }}</dd>
            </div>
            
            <div>
              <dt class="text-sm font-medium text-gray-500">Email</dt>
              <dd class="text-sm text-gray-900">{{ user?.email }}</dd>
            </div>
            
            <div>
              <dt class="text-sm font-medium text-gray-500">Phone Number</dt>
              <dd class="text-sm text-gray-900">{{ user?.phoneNumber }}</dd>
            </div>
            
            <div>
              <dt class="text-sm font-medium text-gray-500">Role</dt>
              <dd class="text-sm">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                      :class="getRoleColor(user?.role)">
                  {{ user?.role }}
                </span>
              </dd>
            </div>
            
            <div>
              <dt class="text-sm font-medium text-gray-500">Member Since</dt>
              <dd class="text-sm text-gray-900">{{ formatDate(user?.createdAt) }}</dd>
            </div>
          </div>
        </div>
        
        <!-- Reading Statistics -->
        <div class="card">
          <h2 class="text-lg font-serif font-semibold text-gray-900 mb-4">Reading Statistics</h2>
          
          <div class="grid grid-cols-2 md:grid-cols-4 gap-6">
            <div class="text-center">
              <div class="text-2xl font-bold text-primary-600">{{ stats.totalBorrowed || 0 }}</div>
              <div class="text-sm text-gray-500">Books Borrowed</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-success-600">{{ stats.currentlyBorrowed || 0 }}</div>
              <div class="text-sm text-gray-500">Currently Reading</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-orange-600">{{ stats.overdue || 0 }}</div>
              <div class="text-sm text-gray-500">Overdue</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-accent-600">{{ stats.favoriteCategory || 'N/A' }}</div>
              <div class="text-sm text-gray-500">Favorite Category</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Sidebar -->
      <div class="lg:col-span-1 space-y-6">
        <!-- Profile Avatar -->
        <div class="card text-center">
          <div class="w-24 h-24 rounded-full bg-primary-100 flex items-center justify-center mx-auto mb-4">
            <span class="text-3xl font-medium text-primary-600">
              {{ user?.firstName?.charAt(0) }}{{ user?.lastName?.charAt(0) }}
            </span>
          </div>
          <h3 class="text-lg font-medium text-gray-900">{{ user?.firstName }} {{ user?.lastName }}</h3>
          <p class="text-sm text-gray-500">{{ user?.role }}</p>
        </div>
        
        <!-- Quick Actions -->
        <div class="card">
          <h3 class="text-lg font-serif font-semibold text-gray-900 mb-4">Quick Actions</h3>
          <div class="space-y-3">
            <router-link to="/my-books" class="block w-full btn-outline text-center">
              View My Books
            </router-link>
            <router-link to="/books" class="block w-full btn-outline text-center">
              Browse Catalog
            </router-link>
            <button @click="downloadData" class="w-full btn-outline">
              Download My Data
            </button>
          </div>
        </div>
        
        <!-- Recent Activity -->
        <div class="card">
          <h3 class="text-lg font-serif font-semibold text-gray-900 mb-4">Recent Activity</h3>
          
          <div v-if="recentActivity.length > 0" class="space-y-3">
            <div v-for="activity in recentActivity" :key="activity.id" 
                 class="flex items-center space-x-3 p-2 rounded-lg hover:bg-gray-50">
              <div class="w-8 h-10 bg-gradient-to-br from-accent-400 to-primary-500 rounded flex items-center justify-center flex-shrink-0">
                <BookOpenIcon class="h-4 w-4 text-white" />
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900 truncate">{{ activity.book.title }}</p>
                <p class="text-xs text-gray-500">{{ formatActivityDate(activity.createdAt) }}</p>
              </div>
              <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium"
                    :class="getActivityStatusColor(activity.status)">
                {{ activity.status }}
              </span>
            </div>
          </div>
          
          <div v-else class="text-center py-6 text-gray-500">
            <BookOpenIcon class="mx-auto h-8 w-8 text-gray-400 mb-2" />
            <p class="text-sm">No recent activity</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import axios from 'axios'
import { BookOpenIcon } from '@heroicons/vue/24/outline'

const authStore = useAuthStore()
const editMode = ref(false)
const saving = ref(false)

const stats = ref({
  totalBorrowed: 0,
  currentlyBorrowed: 0,
  overdue: 0,
  favoriteCategory: ''
})

const recentActivity = ref([])

const user = computed(() => authStore.user)

const profileForm = reactive({
  firstName: '',
  lastName: '',
  email: '',
  phoneNumber: '',
  password: ''
})

const initializeForm = () => {
  if (user.value) {
    profileForm.firstName = user.value.firstName
    profileForm.lastName = user.value.lastName
    profileForm.email = user.value.email
    profileForm.phoneNumber = user.value.phoneNumber
    profileForm.password = ''
  }
}

const updateProfile = async () => {
  try {
    saving.value = true
    
    const updateData: any = {
      firstName: profileForm.firstName,
      lastName: profileForm.lastName,
      email: profileForm.email,
      phoneNumber: profileForm.phoneNumber
    }
    
    if (profileForm.password) {
      updateData.password = profileForm.password
    }
    
    await authStore.updateProfile(updateData)
    
    editMode.value = false
    profileForm.password = ''
    alert('Profile updated successfully!')
  } catch (error) {
    alert('Failed to update profile')
  } finally {
    saving.value = false
  }
}

const loadStats = async () => {
  try {
    // Load user statistics
    const borrowResponse = await axios.get('/api/borrows/my-books?size=1000')
    const allRecords = borrowResponse.data.content
    
    stats.value.totalBorrowed = allRecords.length
    stats.value.currentlyBorrowed = allRecords.filter((r: any) => r.status === 'BORROWED').length
    stats.value.overdue = allRecords.filter((r: any) => r.status === 'OVERDUE').length
    
    // Calculate favorite category
    const categoryCount: { [key: string]: number } = {}
    allRecords.forEach((record: any) => {
      const category = record.book.category
      categoryCount[category] = (categoryCount[category] || 0) + 1
    })
    
    stats.value.favoriteCategory = Object.keys(categoryCount).reduce((a, b) => 
      categoryCount[a] > categoryCount[b] ? a : b, 'N/A'
    )
  } catch (error) {
    console.error('Failed to load stats:', error)
  }
}

const loadRecentActivity = async () => {
  try {
    const response = await axios.get('/api/borrows/my-books?size=5&page=0')
    recentActivity.value = response.data.content
  } catch (error) {
    console.error('Failed to load recent activity:', error)
  }
}

const downloadData = () => {
  // TODO: Implement data export functionality
  alert('Data export functionality coming soon!')
}

const formatDate = (dateString?: string) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatActivityDate = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = now.getTime() - date.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays === 1) return 'Yesterday'
  if (diffDays < 7) return `${diffDays} days ago`
  return date.toLocaleDateString()
}

const getRoleColor = (role?: string) => {
  switch (role) {
    case 'ADMIN':
      return 'bg-danger-100 text-danger-800'
    case 'LIBRARIAN':
      return 'bg-primary-100 text-primary-800'
    case 'STUDENT':
      return 'bg-success-100 text-success-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const getActivityStatusColor = (status: string) => {
  switch (status) {
    case 'BORROWED':
      return 'bg-primary-100 text-primary-800'
    case 'RETURNED':
      return 'bg-success-100 text-success-800'
    case 'OVERDUE':
      return 'bg-danger-100 text-danger-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

onMounted(() => {
  initializeForm()
  loadStats()
  loadRecentActivity()
})
</script>