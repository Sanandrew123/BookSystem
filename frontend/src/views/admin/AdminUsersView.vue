<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-serif font-bold text-gray-900">User Management</h1>
        <p class="mt-1 text-sm text-gray-600">Manage library users and their permissions</p>
      </div>
      <button @click="showCreateModal = true" class="btn-primary">
        <PlusIcon class="w-5 h-5 mr-2" />
        Add User
      </button>
    </div>

    <!-- Filters -->
    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="form-label">Search</label>
          <input v-model="filters.search" type="text" class="input-field" 
                 placeholder="Search users..." @input="debouncedSearch">
        </div>
        <div>
          <label class="form-label">Role</label>
          <select v-model="filters.role" @change="loadUsers" class="input-field">
            <option value="">All Roles</option>
            <option value="ADMIN">Admin</option>
            <option value="LIBRARIAN">Librarian</option>
            <option value="STUDENT">Student</option>
          </select>
        </div>
        <div>
          <label class="form-label">Status</label>
          <select v-model="filters.status" @change="loadUsers" class="input-field">
            <option value="">All Status</option>
            <option value="active">Active</option>
            <option value="inactive">Inactive</option>
          </select>
        </div>
        <div class="flex items-end">
          <button @click="clearFilters" class="btn-outline w-full">
            Clear Filters
          </button>
        </div>
      </div>
    </div>

    <!-- Users Table -->
    <div class="card p-0">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                User
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Contact
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Role
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Status
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Joined
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
            
            <tr v-else-if="!users.length">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500">
                No users found
              </td>
            </tr>
            
            <tr v-else v-for="user in users" :key="user.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center">
                    <span class="text-sm font-medium text-primary-600">
                      {{ user.firstName.charAt(0) }}{{ user.lastName.charAt(0) }}
                    </span>
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900">
                      {{ user.firstName }} {{ user.lastName }}
                    </div>
                    <div class="text-sm text-gray-500">{{ user.username }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ user.email }}</div>
                <div class="text-sm text-gray-500">{{ user.phoneNumber }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                      :class="getRoleColor(user.role)">
                  {{ user.role }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                      :class="user.enabled ? 'bg-success-100 text-success-800' : 'bg-danger-100 text-danger-800'">
                  {{ user.enabled ? 'Active' : 'Inactive' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDate(user.createdAt) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                <button @click="editUser(user)" class="text-primary-600 hover:text-primary-900">
                  Edit
                </button>
                <button @click="toggleUserStatus(user)" 
                        :class="user.enabled ? 'text-danger-600 hover:text-danger-900' : 'text-success-600 hover:text-success-900'">
                  {{ user.enabled ? 'Disable' : 'Enable' }}
                </button>
                <button @click="deleteUser(user)" class="text-danger-600 hover:text-danger-900">
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

    <!-- Create/Edit User Modal -->
    <div v-if="showCreateModal || showEditModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" @click="closeModals"></div>
        
        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6">
          <div>
            <h3 class="text-lg leading-6 font-serif font-semibold text-gray-900 mb-4">
              {{ showCreateModal ? 'Add New User' : 'Edit User' }}
            </h3>
            
            <form @submit.prevent="saveUser" class="space-y-4">
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="form-label">First Name</label>
                  <input v-model="userForm.firstName" type="text" required class="input-field">
                </div>
                <div>
                  <label class="form-label">Last Name</label>
                  <input v-model="userForm.lastName" type="text" required class="input-field">
                </div>
              </div>
              
              <div>
                <label class="form-label">Username</label>
                <input v-model="userForm.username" type="text" required class="input-field">
              </div>
              
              <div>
                <label class="form-label">Email</label>
                <input v-model="userForm.email" type="email" required class="input-field">
              </div>
              
              <div>
                <label class="form-label">Phone Number</label>
                <input v-model="userForm.phoneNumber" type="tel" required class="input-field">
              </div>
              
              <div v-if="showCreateModal">
                <label class="form-label">Password</label>
                <input v-model="userForm.password" type="password" required class="input-field">
              </div>
              
              <div>
                <label class="form-label">Role</label>
                <select v-model="userForm.role" required class="input-field">
                  <option value="STUDENT">Student</option>
                  <option value="LIBRARIAN">Librarian</option>
                  <option value="ADMIN">Admin</option>
                </select>
              </div>
              
              <div class="flex items-center">
                <input v-model="userForm.enabled" type="checkbox" class="mr-2">
                <label class="text-sm text-gray-700">Account Enabled</label>
              </div>
              
              <div class="flex justify-end space-x-3 pt-4">
                <button type="button" @click="closeModals" class="btn-outline">
                  Cancel
                </button>
                <button type="submit" :disabled="saving" class="btn-primary">
                  {{ saving ? 'Saving...' : (showCreateModal ? 'Create User' : 'Update User') }}
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
import { PlusIcon } from '@heroicons/vue/24/outline'

const users = ref([])
const loading = ref(true)
const saving = ref(false)
const showCreateModal = ref(false)
const showEditModal = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)

const filters = reactive({
  search: '',
  role: '',
  status: ''
})

const userForm = reactive({
  id: null,
  firstName: '',
  lastName: '',
  username: '',
  email: '',
  phoneNumber: '',
  password: '',
  role: 'STUDENT',
  enabled: true
})

let searchTimeout: NodeJS.Timeout

const debouncedSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    currentPage.value = 1
    loadUsers()
  }, 500)
}

const loadUsers = async () => {
  try {
    loading.value = true
    const params = new URLSearchParams({
      page: (currentPage.value - 1).toString(),
      size: pageSize.value.toString()
    })
    
    if (filters.search) params.append('search', filters.search)
    if (filters.role) params.append('role', filters.role)
    if (filters.status === 'active') params.append('enabled', 'true')
    if (filters.status === 'inactive') params.append('enabled', 'false')
    
    const response = await axios.get(`/api/admin/users?${params}`)
    users.value = response.data.content
    totalElements.value = response.data.totalElements
    totalPages.value = response.data.totalPages
  } catch (error) {
    console.error('Failed to load users:', error)
  } finally {
    loading.value = false
  }
}

const clearFilters = () => {
  filters.search = ''
  filters.role = ''
  filters.status = ''
  currentPage.value = 1
  loadUsers()
}

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    loadUsers()
  }
}

const editUser = (user: any) => {
  Object.assign(userForm, user)
  userForm.password = '' // Don't pre-fill password
  showEditModal.value = true
}

const saveUser = async () => {
  try {
    saving.value = true
    
    if (showCreateModal.value) {
      await axios.post('/api/admin/users', userForm)
    } else {
      await axios.put(`/api/admin/users/${userForm.id}`, userForm)
    }
    
    closeModals()
    loadUsers()
  } catch (error) {
    console.error('Failed to save user:', error)
  } finally {
    saving.value = false
  }
}

const toggleUserStatus = async (user: any) => {
  try {
    await axios.put(`/api/admin/users/${user.id}/status`, { enabled: !user.enabled })
    loadUsers()
  } catch (error) {
    console.error('Failed to toggle user status:', error)
  }
}

const deleteUser = async (user: any) => {
  if (confirm(`Are you sure you want to delete ${user.firstName} ${user.lastName}?`)) {
    try {
      await axios.delete(`/api/admin/users/${user.id}`)
      loadUsers()
    } catch (error) {
      console.error('Failed to delete user:', error)
    }
  }
}

const closeModals = () => {
  showCreateModal.value = false
  showEditModal.value = false
  Object.assign(userForm, {
    id: null,
    firstName: '',
    lastName: '',
    username: '',
    email: '',
    phoneNumber: '',
    password: '',
    role: 'STUDENT',
    enabled: true
  })
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

const getRoleColor = (role: string) => {
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

onMounted(() => {
  loadUsers()
})
</script>