<script setup lang="ts">
import AdminHeader from '@/components/admin/AdminHeader.vue'
</script>

<template>
  <div class="min-h-screen bg-surface">
    <AdminHeader />
    <main class="mx-auto max-w-7xl px-4 py-6 md:px-6">
      <RouterView />
    </main>
  </div>
</template>
