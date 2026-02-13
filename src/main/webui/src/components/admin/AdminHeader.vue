<script setup lang="ts">
import AdminNav from './AdminNav.vue'
import { useAuth } from '@/composables/useAuth'

const { user } = useAuth()
</script>

<template>
  <header role="banner" class="bg-dark" data-testid="admin-header">
    <div class="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 md:px-6">
      <h2 class="text-lg font-bold text-white">Administration</h2>

      <AdminNav class="hidden md:block" />

      <div v-if="user" class="flex items-center gap-2" data-testid="user-info">
        <span class="text-sm text-white/80">{{ user.name }}</span>
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
          fill="currentColor"
          class="h-6 w-6 text-white/80"
          aria-hidden="true"
        >
          <path
            fill-rule="evenodd"
            d="M18.685 19.097A9.723 9.723 0 0 0 21.75 12c0-5.385-4.365-9.75-9.75-9.75S2.25 6.615 2.25 12a9.723 9.723 0 0 0 3.065 7.097A9.716 9.716 0 0 0 12 21.75a9.716 9.716 0 0 0 6.685-2.653Zm-2.54-1.228a7.493 7.493 0 0 0-8.29 0 7.5 7.5 0 0 1 8.29 0ZM12 13.5a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z"
            clip-rule="evenodd"
          />
        </svg>
      </div>
    </div>

    <!-- Mobile navigation -->
    <div class="overflow-x-auto border-t border-white/10 md:hidden">
      <div class="mx-auto max-w-7xl px-4">
        <AdminNav />
      </div>
    </div>
  </header>
</template>
