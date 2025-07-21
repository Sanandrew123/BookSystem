<template>
  <nav class="fixed top-0 left-0 right-0 z-50 glass-effect border-b border-gray-200">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <div class="flex items-center">
          <router-link to="/" class="flex items-center space-x-3">
            <div class="w-8 h-8 bg-gradient-to-br from-primary-500 to-accent-600 rounded-lg flex items-center justify-center">
              <BookOpenIcon class="w-5 h-5 text-white" />
            </div>
            <span class="font-serif text-xl font-semibold text-gray-900">LibraryMS</span>
          </router-link>
        </div>
        
        <div class="hidden sm:flex sm:items-center sm:space-x-8">
          <router-link v-for="item in navigation" :key="item.name" :to="item.href" 
                       class="nav-link" 
                       :class="{ 'nav-link-active': $route.path === item.href }">
            {{ item.name }}
          </router-link>
        </div>
        
        <div class="flex items-center space-x-4">
          <div class="hidden sm:flex sm:items-center sm:space-x-4">
            <button @click="toggleNotifications" class="p-2 text-gray-400 hover:text-gray-500 relative">
              <BellIcon class="w-5 h-5" />
              <span v-if="notifications.length > 0" 
                    class="absolute -top-1 -right-1 w-4 h-4 bg-danger-500 text-white text-xs rounded-full flex items-center justify-center">
                {{ notifications.length }}
              </span>
            </button>
            
            <div class="relative">
              <button @click="toggleUserMenu" class="flex items-center space-x-3 p-2 rounded-lg hover:bg-gray-100">
                <div class="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
                  <UserIcon class="w-5 h-5 text-primary-600" />
                </div>
                <span class="font-medium text-gray-700">{{ user?.firstName }}</span>
                <ChevronDownIcon class="w-4 h-4 text-gray-500" />
              </button>
              
              <!-- User Dropdown Menu -->
              <div v-if="showUserMenu" 
                   class="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg border border-gray-100 py-2">
                <router-link to="/profile" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">
                  Profile
                </router-link>
                <router-link to="/settings" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">
                  Settings
                </router-link>
                <hr class="my-2">
                <button @click="logout" class="w-full text-left px-4 py-2 text-sm text-danger-600 hover:bg-gray-50">
                  Sign out
                </button>
              </div>
            </div>
          </div>
          
          <!-- Mobile menu button -->
          <button @click="toggleMobileMenu" class="sm:hidden p-2 text-gray-400 hover:text-gray-500">
            <Bars3Icon v-if="!showMobileMenu" class="w-6 h-6" />
            <XMarkIcon v-else class="w-6 h-6" />
          </button>
        </div>
      </div>
    </div>
    
    <!-- Mobile menu -->
    <div v-if="showMobileMenu" class="sm:hidden border-t border-gray-200 bg-white">
      <div class="px-2 pt-2 pb-3 space-y-1">
        <router-link v-for="item in navigation" :key="item.name" :to="item.href" 
                     @click="showMobileMenu = false"
                     class="block px-3 py-2 text-base font-medium text-gray-700 hover:text-primary-600 hover:bg-gray-50 rounded-md">
          {{ item.name }}
        </router-link>
      </div>
      <div class="pt-4 pb-3 border-t border-gray-200">
        <div class="px-5">
          <div class="text-base font-medium text-gray-800">{{ user?.firstName }} {{ user?.lastName }}</div>
          <div class="text-sm text-gray-500">{{ user?.email }}</div>
        </div>
        <div class="mt-3 space-y-1">
          <router-link to="/profile" @click="showMobileMenu = false"
                       class="block px-4 py-2 text-base font-medium text-gray-500 hover:text-gray-800 hover:bg-gray-100">
            Profile
          </router-link>
          <button @click="logout" 
                  class="block w-full text-left px-4 py-2 text-base font-medium text-gray-500 hover:text-gray-800 hover:bg-gray-100">
            Sign out
          </button>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  BookOpenIcon,
  BellIcon,
  UserIcon,
  ChevronDownIcon,
  Bars3Icon,
  XMarkIcon
} from '@heroicons/vue/24/outline'

const router = useRouter()
const authStore = useAuthStore()

const showUserMenu = ref(false)
const showMobileMenu = ref(false)
const showNotifications = ref(false)

const user = computed(() => authStore.user)
const notifications = ref([]) // TODO: Implement notifications

const navigation = computed(() => {
  const baseNav = [
    { name: 'Dashboard', href: '/dashboard' },
    { name: 'Books', href: '/books' },
    { name: 'My Books', href: '/my-books' }
  ]
  
  if (user.value?.role === 'ADMIN' || user.value?.role === 'LIBRARIAN') {
    baseNav.push({ name: 'Manage', href: '/admin' })
  }
  
  return baseNav
})

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
  showMobileMenu.value = false
}

const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
  showUserMenu.value = false
}

const toggleNotifications = () => {
  showNotifications.value = !showNotifications.value
}

const logout = async () => {
  await authStore.logout()
  router.push('/auth/login')
}
</script>