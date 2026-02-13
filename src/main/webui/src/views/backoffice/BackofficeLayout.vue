<script setup lang="ts">
import BackofficeHeader from '@/components/backoffice/BackofficeHeader.vue'
</script>

<template>
  <div class="min-h-screen bg-surface">
    <BackofficeHeader />
    <main class="mx-auto max-w-7xl px-4 py-6 md:px-6">
      <RouterView />
    </main>
  </div>
</template>
