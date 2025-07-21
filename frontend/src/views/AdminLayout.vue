<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Admin Sidebar -->
    <div class="fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform transition-transform lg:translate-x-0"
         :class="{ '-translate-x-full': !sidebarOpen, 'translate-x-0': sidebarOpen }">
      
      <!-- Sidebar Header -->
      <div class="flex items-center justify-between h-16 px-6 border-b border-gray-200">
        <h2 class="text-lg font-serif font-semibold text-gray-900">Admin Panel</h2>
        <button @click="sidebarOpen = false" class="lg:hidden">
          <XMarkIcon class="w-6 h-6 text-gray-500" />
        </button>
      </div>
      
      <!-- Navigation -->
      <nav class="mt-8 px-4 space-y-2">
        <router-link v-for="item in navigationItems" :key="item.name" :to="item.href"
                     class="flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-colors"
                     :class="{ 'bg-primary-50 text-primary-700': $route.path === item.href, 
                              'text-gray-700 hover:bg-gray-100': $route.path !== item.href }">
          <component :is="item.icon" class="mr-3 h-5 w-5" />
          {{ item.name }}
        </router-link>
      </nav>
    </div>
    
    <!-- Main Content -->
    <div class="lg:pl-64">
      <!-- Mobile Header -->
      <div class="lg:hidden flex items-center justify-between h-16 px-4 bg-white border-b border-gray-200">
        <button @click="sidebarOpen = true">
          <Bars3Icon class="w-6 h-6 text-gray-500" />
        </button>
        <h1 class="text-lg font-semibold text-gray-900">Admin Panel</h1>
        <div></div>
      </div>
      
      <!-- Page Content -->
      <main class="p-6">
        <RouterView />
      </main>
    </div>
    
    <!-- Mobile Sidebar Overlay -->
    <div v-if="sidebarOpen" @click="sidebarOpen = false" 
         class="fixed inset-0 z-40 bg-gray-600 bg-opacity-50 lg:hidden"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthStore } from '../stores/auth'
import {
  HomeIcon,
  BookOpenIcon,
  UsersIcon,
  ClipboardDocumentListIcon,
  ChartBarIcon,
  Bars3Icon,
  XMarkIcon
} from '@heroicons/vue/24/outline'

const authStore = useAuthStore()
const sidebarOpen = ref(false)

const navigationItems = computed(() => {
  const baseItems = [
    { name: 'Dashboard', href: '/admin', icon: HomeIcon },
    { name: 'Books', href: '/admin/books', icon: BookOpenIcon },
    { name: 'Borrow Records', href: '/admin/borrows', icon: ClipboardDocumentListIcon },
  ]
  
  if (authStore.isAdmin) {
    baseItems.splice(2, 0, { name: 'Users', href: '/admin/users', icon: UsersIcon })
  }
  
  return baseItems
})
</script>