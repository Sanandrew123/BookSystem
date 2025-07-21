<template>
  <div class="min-h-screen bg-gradient-to-br from-accent-50 via-primary-50 to-secondary-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div class="text-center">
        <div class="mx-auto h-16 w-16 bg-gradient-to-br from-primary-500 to-accent-600 rounded-2xl flex items-center justify-center shadow-lg">
          <BookOpenIcon class="h-8 w-8 text-white" />
        </div>
        <h1 class="mt-6 text-3xl font-serif font-bold text-gray-900">
          Library Management System
        </h1>
        <p class="mt-2 text-sm text-gray-600">
          Discover, borrow, and manage your reading journey
        </p>
      </div>
      
      <div class="bg-white/80 backdrop-blur-sm rounded-2xl shadow-xl p-8 border border-white/20">
        <RouterView />
      </div>
      
      <div class="text-center">
        <p class="text-xs text-gray-500">
          © 2024 Library Management System. Built with modern web technologies.
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { BookOpenIcon } from '@heroicons/vue/24/solid'
</script>