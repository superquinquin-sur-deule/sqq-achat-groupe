<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cartStore'
import CartSummary from '@/components/cart/CartSummary.vue'
import Button from '@/components/ui/Button.vue'
import Stepper from '@/components/ui/Stepper.vue'

const cartStore = useCartStore()
const route = useRoute()
const router = useRouter()
const venteId = Number(route.params.venteId)

const steps = [
  { label: 'Panier' },
  { label: 'Coordonnées' },
  { label: 'Créneau' },
  { label: 'Paiement' },
]

function goToCatalogue() {
  router.push({ name: 'home', params: { venteId } })
}
</script>

<template>
  <main data-testid="cart-view" class="mx-auto max-w-3xl px-4 py-8">
    <Stepper :steps="steps" :current-step="1" class="mb-8" />

    <h1 class="text-2xl font-bold text-dark">Panier</h1>

    <section v-if="!cartStore.isEmpty" aria-label="Votre panier" class="mt-6">
      <CartSummary />
    </section>

    <div
      v-else
      data-testid="empty-cart"
      class="mt-12 flex flex-col items-center justify-center text-center"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        stroke-width="1.5"
        stroke="currentColor"
        class="h-12 w-12 text-brown"
        aria-hidden="true"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          d="M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 0 0-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 0 0-16.536-1.84M7.5 14.25 5.106 5.272M6 20.25a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Zm12.75 0a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Z"
        />
      </svg>
      <p class="mt-4 text-lg font-medium text-dark">Votre panier est vide</p>
      <p class="mt-1 text-sm text-brown">Ajoutez des produits depuis le catalogue</p>
      <Button
        data-testid="discover-products"
        variant="secondary"
        class="mt-6"
        @click="goToCatalogue"
      >
        Découvrir nos produits
      </Button>
    </div>
  </main>
</template>
