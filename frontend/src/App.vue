<template>
  <div id="app" class="min-h-screen bg-gray-50">
    <NavBar v-if="!isAuthPage" />
    <main :class="{ 'pt-16': !isAuthPage }">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'

const route = useRoute()

const isAuthPage = computed(() => {
  return route.path.startsWith('/auth')
})
</script>

<style scoped>
#app {
  font-family: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
</style>