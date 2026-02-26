<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { useCartStore } from '@/stores/cartStore'

const cartStore = useCartStore()
const route = useRoute()

const venteId = computed(() => route.params.venteId as string | undefined)
const homeLink = computed(() =>
  venteId.value ? { name: 'home', params: { venteId: venteId.value } } : { name: 'ventes' },
)
const cartLink = computed(() =>
  venteId.value ? { name: 'cart', params: { venteId: venteId.value } } : { name: 'ventes' },
)
</script>

<template>
  <header class="bg-dark text-white">
    <div class="mx-auto flex items-center justify-between px-4 py-3">
      <RouterLink :to="homeLink" class="flex items-center gap-2 text-white no-underline">
        <img src="/logo.svg" alt="SuperQuinquin" class="h-11 w-11" />
        <span class="text-xl font-bold text-primary">SuperQuinquin</span>
      </RouterLink>

      <RouterLink
        :to="cartLink"
        class="relative flex min-h-[44px] min-w-[44px] items-center justify-center rounded-lg bg-primary px-3 py-2 text-sm font-semibold text-dark no-underline hover:opacity-90 focus:outline-2 focus:outline-offset-2 focus:outline-dark"
        aria-label="Panier"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="h-5 w-5"
          aria-hidden="true"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 0 0-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 0 0-16.536-1.84M7.5 14.25 5.106 5.272M6 20.25a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Zm12.75 0a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Z"
          />
        </svg>
        <span
          v-if="cartStore.itemCount > 0"
          data-testid="cart-count"
          role="status"
          aria-live="polite"
          class="absolute -right-1.5 -top-1.5 flex h-5 w-5 items-center justify-center rounded-full bg-dark text-xs font-bold text-white shadow-sm"
        >
          {{ cartStore.itemCount }}
        </span>
      </RouterLink>
    </div>
  </header>
</template>
