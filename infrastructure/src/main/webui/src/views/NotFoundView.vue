<script setup lang="ts">
import { useRouter } from 'vue-router'
import Button from '@/components/ui/Button.vue'

const router = useRouter()
</script>

<template>
  <main class="mx-auto max-w-2xl px-4 py-8 md:px-6">
    <div class="flex flex-col items-center gap-6" data-testid="not-found">
      <div class="flex h-16 w-16 items-center justify-center rounded-full bg-gray-400">
        <svg
          class="h-8 w-8 text-white"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="2.5"
          stroke="currentColor"
          aria-hidden="true"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126zM12 15.75h.007v.008H12v-.008z"
          />
        </svg>
      </div>

      <div class="text-center">
        <h1 class="text-2xl font-bold text-dark" data-testid="not-found-title">Page introuvable</h1>
        <p class="mt-2 text-brown" data-testid="not-found-message">
          La page que vous recherchez n'existe pas ou a été déplacée.
        </p>
      </div>

      <Button class="w-full sm:w-auto sm:min-w-[200px]" @click="router.push({ name: 'ventes' })">
        Retour à l'accueil
      </Button>
    </div>
  </main>
</template>
