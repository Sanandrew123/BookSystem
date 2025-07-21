import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import type { User, LoginRequest, RegisterRequest } from '../types'

const API_URL = 'http://localhost:8080/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!token.value && !!user.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isLibrarian = computed(() => user.value?.role === 'LIBRARIAN' || user.value?.role === 'ADMIN')

  // Set up axios interceptor for token
  if (token.value) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
  }

  const setAuthData = (userData: User, userToken: string) => {
    user.value = userData
    token.value = userToken
    localStorage.setItem('token', userToken)
    axios.defaults.headers.common['Authorization'] = `Bearer ${userToken}`
  }

  const clearAuthData = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    delete axios.defaults.headers.common['Authorization']
  }

  const login = async (credentials: LoginRequest) => {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await axios.post(`${API_URL}/auth/login`, credentials)
      const { user: userData, token: userToken } = response.data
      
      setAuthData(userData, userToken)
      return { success: true }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Login failed'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const register = async (userData: RegisterRequest) => {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await axios.post(`${API_URL}/auth/register`, userData)
      const { user: newUser, token: userToken } = response.data
      
      setAuthData(newUser, userToken)
      return { success: true }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Registration failed'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const logout = async () => {
    clearAuthData()
  }

  const fetchUser = async () => {
    if (!token.value) return
    
    try {
      const response = await axios.get(`${API_URL}/auth/me`)
      user.value = response.data
    } catch (err) {
      // Token is invalid, clear auth data
      clearAuthData()
    }
  }

  const updateProfile = async (profileData: Partial<User>) => {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await axios.put(`${API_URL}/users/profile`, profileData)
      user.value = response.data
      return { success: true }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Profile update failed'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  return {
    user,
    token,
    isLoading,
    error,
    isAuthenticated,
    isAdmin,
    isLibrarian,
    login,
    register,
    logout,
    fetchUser,
    updateProfile
  }
})