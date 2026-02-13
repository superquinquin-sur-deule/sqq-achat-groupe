<script setup lang="ts">
import Header from '@/components/layout/Header.vue'
import Toast from '@/components/ui/Toast.vue'
import { useToast } from '@/composables/useToast'

const { toasts, remove } = useToast()
</script>

<template>
  <a href="#main-content" class="sr-only focus:not-sr-only focus:absolute focus:z-50 focus:bg-primary focus:p-2 focus:text-dark">
    Aller au contenu principal
  </a>
  <Header />
  <main id="main-content" class="min-h-screen bg-surface">
    <router-view />
  </main>
  <Toast
    v-for="toast in toasts"
    :key="toast.id"
    :message="toast.message"
    :variant="toast.variant"
    @close="remove(toast.id)"
  />
</template>
