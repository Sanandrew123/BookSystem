<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-2xl font-serif font-semibold text-gray-900 text-center">
        Create Account
      </h2>
      <p class="mt-2 text-sm text-gray-600 text-center">
        Join our library community today
      </p>
    </div>
    
    <form @submit.prevent="handleSubmit" class="space-y-4">
      <div v-if="errorMessage" class="bg-danger-50 border border-danger-200 text-danger-700 px-4 py-3 rounded-lg text-sm">
        {{ errorMessage }}
      </div>
      
      <div class="grid grid-cols-2 gap-4">
        <div>
          <label for="firstName" class="form-label">First Name</label>
          <input
            id="firstName"
            v-model="form.firstName"
            type="text"
            required
            class="input-field"
            placeholder="John"
            :disabled="isLoading"
          />
        </div>
        
        <div>
          <label for="lastName" class="form-label">Last Name</label>
          <input
            id="lastName"
            v-model="form.lastName"
            type="text"
            required
            class="input-field"
            placeholder="Doe"
            :disabled="isLoading"
          />
        </div>
      </div>
      
      <div>
        <label for="username" class="form-label">Username</label>
        <input
          id="username"
          v-model="form.username"
          type="text"
          required
          class="input-field"
          placeholder="Choose a unique username"
          :disabled="isLoading"
        />
      </div>
      
      <div>
        <label for="email" class="form-label">Email Address</label>
        <input
          id="email"
          v-model="form.email"
          type="email"
          required
          class="input-field"
          placeholder="john.doe@example.com"
          :disabled="isLoading"
        />
      </div>
      
      <div>
        <label for="phoneNumber" class="form-label">Phone Number</label>
        <input
          id="phoneNumber"
          v-model="form.phoneNumber"
          type="tel"
          required
          class="input-field"
          placeholder="+1 (555) 123-4567"
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
            placeholder="At least 6 characters"
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
        <div class="mt-1">
          <div class="flex space-x-1">
            <div v-for="i in 4" :key="i" 
                 class="flex-1 h-1 rounded-full transition-colors"
                 :class="passwordStrength >= i ? getStrengthColor(passwordStrength) : 'bg-gray-200'">
            </div>
          </div>
          <p class="text-xs text-gray-500 mt-1">
            {{ getStrengthText(passwordStrength) }}
          </p>
        </div>
      </div>
      
      <div>
        <label for="confirmPassword" class="form-label">Confirm Password</label>
        <input
          id="confirmPassword"
          v-model="confirmPassword"
          type="password"
          required
          class="input-field"
          :class="{ 'border-danger-300': confirmPassword && form.password !== confirmPassword }"
          placeholder="Confirm your password"
          :disabled="isLoading"
        />
        <p v-if="confirmPassword && form.password !== confirmPassword" 
           class="text-xs text-danger-600 mt-1">
          Passwords do not match
        </p>
      </div>
      
      <div class="flex items-start">
        <input
          id="agreeTerms"
          v-model="agreeTerms"
          type="checkbox"
          required
          class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded mt-1"
        />
        <label for="agreeTerms" class="ml-3 text-sm text-gray-700">
          I agree to the 
          <a href="#" class="text-primary-600 hover:text-primary-500">Terms of Service</a>
          and 
          <a href="#" class="text-primary-600 hover:text-primary-500">Privacy Policy</a>
        </label>
      </div>
      
      <button
        type="submit"
        :disabled="isLoading || !canSubmit"
        class="w-full btn-primary flex items-center justify-center py-3 text-base font-medium disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <div v-if="isLoading" class="flex items-center">
          <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Creating account...
        </div>
        <span v-else>Create Account</span>
      </button>
    </form>
    
    <div class="text-center">
      <p class="text-sm text-gray-600">
        Already have an account?
        <router-link to="/auth/login" class="font-medium text-primary-600 hover:text-primary-500 transition-colors">
          Sign in here
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
import type { RegisterRequest } from '../types'

const router = useRouter()
const authStore = useAuthStore()

const form = ref<RegisterRequest>({
  username: '',
  email: '',
  password: '',
  firstName: '',
  lastName: '',
  phoneNumber: ''
})

const confirmPassword = ref('')
const showPassword = ref(false)
const agreeTerms = ref(false)
const errorMessage = ref('')

const isLoading = computed(() => authStore.isLoading)

const passwordStrength = computed(() => {
  const password = form.value.password
  let strength = 0
  
  if (password.length >= 6) strength++
  if (password.match(/[a-z]/)) strength++
  if (password.match(/[A-Z]/)) strength++
  if (password.match(/[0-9]/) || password.match(/[^a-zA-Z0-9]/)) strength++
  
  return strength
})

const canSubmit = computed(() => {
  return form.value.firstName &&
         form.value.lastName &&
         form.value.username &&
         form.value.email &&
         form.value.phoneNumber &&
         form.value.password &&
         confirmPassword.value &&
         form.value.password === confirmPassword.value &&
         passwordStrength.value >= 2 &&
         agreeTerms.value
})

const getStrengthColor = (strength: number) => {
  switch (strength) {
    case 1: return 'bg-danger-500'
    case 2: return 'bg-accent-500'
    case 3: return 'bg-primary-500'
    case 4: return 'bg-success-500'
    default: return 'bg-gray-200'
  }
}

const getStrengthText = (strength: number) => {
  switch (strength) {
    case 0: return 'Enter a password'
    case 1: return 'Weak password'
    case 2: return 'Fair password'
    case 3: return 'Good password'
    case 4: return 'Strong password'
    default: return ''
  }
}

const handleSubmit = async () => {
  errorMessage.value = ''
  
  if (!canSubmit.value) {
    errorMessage.value = 'Please fill in all fields correctly'
    return
  }
  
  const result = await authStore.register(form.value)
  
  if (result.success) {
    router.push('/dashboard')
  } else {
    errorMessage.value = result.error || 'Registration failed'
  }
}
</script>