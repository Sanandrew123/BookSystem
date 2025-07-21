<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-2xl font-serif font-semibold text-gray-900 text-center">
        Welcome Back
      </h2>
      <p class="mt-2 text-sm text-gray-600 text-center">
        Sign in to access your library account
      </p>
    </div>
    
    <form @submit.prevent="handleSubmit" class="space-y-5">
      <div v-if="errorMessage" class="bg-danger-50 border border-danger-200 text-danger-700 px-4 py-3 rounded-lg text-sm">
        {{ errorMessage }}
      </div>
      
      <div>
        <label for="username" class="form-label">Username</label>
        <input
          id="username"
          v-model="form.username"
          type="text"
          required
          class="input-field"
          placeholder="Enter your username"
          :disabled="isLoading"
        />
      </div>
      
      <div>
        <label for="password" class="form-label">Password</label>
        <div class="relative">
          <input
            id="password"
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            required
            class="input-field pr-10"
            placeholder="Enter your password"
            :disabled="isLoading"
          />
          <button
            type="button"
            @click="showPassword = !showPassword"
            class="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-400 hover:text-gray-600"
          >
            <EyeIcon v-if="!showPassword" class="h-5 w-5" />
            <EyeSlashIcon v-else class="h-5 w-5" />
          </button>
        </div>
      </div>
      
      <div class="flex items-center justify-between">
        <label class="flex items-center">
          <input
            v-model="form.rememberMe"
            type="checkbox"
            class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <span class="ml-2 block text-sm text-gray-700">Remember me</span>
        </label>
        
        <a href="#" class="text-sm text-primary-600 hover:text-primary-500">
          Forgot password?
        </a>
      </div>
      
      <button
        type="submit"
        :disabled="isLoading"
        class="w-full btn-primary flex items-center justify-center py-3 text-base font-medium disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <div v-if="isLoading" class="flex items-center">
          <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Signing in...
        </div>
        <span v-else>Sign In</span>
      </button>
    </form>
    
    <div class="text-center">
      <p class="text-sm text-gray-600">
        Don't have an account?
        <router-link to="/auth/register" class="font-medium text-primary-600 hover:text-primary-500 transition-colors">
          Sign up here
        </router-link>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { EyeIcon, EyeSlashIcon } from '@heroicons/vue/24/outline'
import type { LoginRequest } from '../types'

const router = useRouter()
const authStore = useAuthStore()

const form = ref<LoginRequest & { rememberMe: boolean }>({
  username: '',
  password: '',
  rememberMe: false
})

const showPassword = ref(false)
const errorMessage = ref('')

const isLoading = computed(() => authStore.isLoading)

const handleSubmit = async () => {
  errorMessage.value = ''
  
  if (!form.value.username || !form.value.password) {
    errorMessage.value = 'Please fill in all required fields'
    return
  }
  
  const result = await authStore.login({
    username: form.value.username,
    password: form.value.password
  })
  
  if (result.success) {
    router.push('/dashboard')
  } else {
    errorMessage.value = result.error || 'Login failed'
  }
}
</script>