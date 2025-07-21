<template>
  <div class="min-h-screen bg-gradient-to-br from-accent-50 via-primary-50 to-secondary-50 flex items-center justify-center px-4">
    <div class="max-w-md w-full text-center">
      <div class="mb-8">
        <div class="mx-auto h-24 w-24 bg-gradient-to-br from-primary-500 to-accent-600 rounded-full flex items-center justify-center shadow-lg">
          <BookOpenIcon class="h-12 w-12 text-white" />
        </div>
      </div>
      
      <div class="space-y-4">
        <h1 class="text-6xl font-serif font-bold text-gray-900">404</h1>
        <h2 class="text-2xl font-serif font-semibold text-gray-700">Page Not Found</h2>
        <p class="text-gray-600">
          The page you're looking for seems to have wandered off into the stacks. 
          Let's help you find your way back.
        </p>
      </div>
      
      <div class="mt-8 space-y-4">
        <router-link to="/dashboard" class="btn-primary w-full">
          <HomeIcon class="h-5 w-5 mr-2" />
          Go to Dashboard
        </router-link>
        
        <router-link to="/books" class="btn-outline w-full">
          <MagnifyingGlassIcon class="h-5 w-5 mr-2" />
          Browse Books
        </router-link>
        
        <button @click="goBack" class="btn-outline w-full">
          <ArrowLeftIcon class="h-5 w-5 mr-2" />
          Go Back
        </button>
      </div>
      
      <div class="mt-8 text-center">
        <p class="text-xs text-gray-500">
          © 2024 Library Management System. Lost but not forgotten.
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import {
  BookOpenIcon,
  HomeIcon,
  MagnifyingGlassIcon,
  ArrowLeftIcon
} from '@heroicons/vue/24/outline'

const router = useRouter()

const goBack = () => {
  router.back()
}
</script>