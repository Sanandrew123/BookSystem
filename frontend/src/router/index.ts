import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard'
    },
    {
      path: '/auth',
      component: () => import('../views/AuthLayout.vue'),
      children: [
        {
          path: 'login',
          name: 'Login',
          component: () => import('../views/LoginView.vue'),
          meta: { requiresGuest: true }
        },
        {
          path: 'register',
          name: 'Register',
          component: () => import('../views/RegisterView.vue'),
          meta: { requiresGuest: true }
        }
      ]
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('../views/DashboardView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/books',
      name: 'Books',
      component: () => import('../views/BooksView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/books/:id',
      name: 'BookDetail',
      component: () => import('../views/BookDetailView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/my-books',
      name: 'MyBooks',
      component: () => import('../views/MyBooksView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin',
      component: () => import('../views/AdminLayout.vue'),
      meta: { requiresAuth: true, requiresLibrarian: true },
      children: [
        {
          path: '',
          name: 'AdminDashboard',
          component: () => import('../views/admin/AdminDashboardView.vue')
        },
        {
          path: 'books',
          name: 'AdminBooks',
          component: () => import('../views/admin/AdminBooksView.vue')
        },
        {
          path: 'users',
          name: 'AdminUsers',
          component: () => import('../views/admin/AdminUsersView.vue'),
          meta: { requiresAdmin: true }
        },
        {
          path: 'borrows',
          name: 'AdminBorrows',
          component: () => import('../views/admin/AdminBorrowsView.vue')
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('../views/NotFoundView.vue')
    }
  ]
})

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  // Check if user data needs to be fetched
  if (authStore.token && !authStore.user) {
    await authStore.fetchUser()
  }
  
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const requiresGuest = to.matched.some(record => record.meta.requiresGuest)
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin)
  const requiresLibrarian = to.matched.some(record => record.meta.requiresLibrarian)
  
  if (requiresAuth && !authStore.isAuthenticated) {
    next('/auth/login')
  } else if (requiresGuest && authStore.isAuthenticated) {
    next('/dashboard')
  } else if (requiresAdmin && !authStore.isAdmin) {
    next('/dashboard')
  } else if (requiresLibrarian && !authStore.isLibrarian) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router