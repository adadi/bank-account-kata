<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useHealthStore } from '../stores/health'

const store = useHealthStore()

onMounted(() => {
  if (!store.lastCheckedAt) {
    store.checkHealth()
  }
})

const show = computed(() => store.status !== 'UP')
</script>

<template>
  <div v-if="show" class="banner">API unavailable</div>
</template>

<style scoped>
.banner {
  padding: 0.5rem 1rem;
  background: #7a0b0b;
  color: white;
  text-align: center;
}
</style>

