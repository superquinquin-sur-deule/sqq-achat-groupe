<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cartStore'
import Card from '@/components/ui/Card.vue'
import Button from '@/components/ui/Button.vue'
import CartItemComponent from '@/components/cart/CartItem.vue'

const cartStore = useCartStore()
const route = useRoute()
const router = useRouter()
const venteId = computed(() => route.params.venteId as string)

const formattedTotal = computed(() =>
  new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(cartStore.total),
)

function goToCheckout() {
  router.push({ name: 'checkout', params: { venteId: venteId.value } })
}

function goToCatalogue() {
  router.push({ name: 'home', params: { venteId: venteId.value } })
}
</script>

<template>
  <Card>
    <div class="space-y-0">
      <CartItemComponent v-for="item in cartStore.items" :key="item.productId" :item="item" />
    </div>

    <div
      class="mt-4 rounded-lg bg-surface border-t-2 border-primary p-4 flex items-center justify-between"
    >
      <span class="text-xl font-bold text-dark">Total</span>
      <span data-testid="cart-total" class="text-xl font-bold text-dark">{{ formattedTotal }}</span>
    </div>

    <div class="mt-4 flex flex-col gap-2 sm:flex-row-reverse">
      <Button variant="primary" size="lg" class="w-full sm:w-auto sm:flex-1" data-testid="checkout-button" @click="goToCheckout">
        Valider mon panier
      </Button>
      <Button variant="secondary" size="lg" class="w-full sm:w-auto sm:flex-1" data-testid="continue-shopping" @click="goToCatalogue">
        Continuer mes achats
      </Button>
    </div>
  </Card>
</template>
