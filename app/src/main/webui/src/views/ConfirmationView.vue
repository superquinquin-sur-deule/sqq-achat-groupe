<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { OrderDetailResponse } from '@/api/generated/model'
import { getApiOrdersId } from '@/api/generated/orders/orders'
import { useCartStore } from '@/stores/cartStore'
import Card from '@/components/ui/Card.vue'
import Button from '@/components/ui/Button.vue'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const order = ref<OrderDetailResponse | null>(null)
const isLoading = ref(true)

function formatPrice(price: number): string {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(price)
}

function formatTime(time: string): string {
  return time.substring(0, 5).replace(':', 'h')
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr + 'T00:00:00')
  return date.toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })
}

onMounted(async () => {
  const orderId = route.query.orderId as string | undefined
  if (!orderId) {
    router.replace('/')
    return
  }

  try {
    const response = await getApiOrdersId(orderId)
    order.value = response.data.data ?? null
    cartStore.clearCart()
  } catch {
    router.replace('/')
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="mx-auto max-w-[640px] px-4 py-8">
    <div v-if="isLoading" class="flex justify-center py-12">
      <svg
        class="h-8 w-8 animate-spin text-dark"
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        aria-label="Chargement"
      >
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
      </svg>
    </div>

    <div v-else-if="order" class="flex flex-col items-center gap-6">
      <!-- Check icon -->
      <div class="flex h-16 w-16 items-center justify-center rounded-full bg-primary" role="status">
        <svg class="h-8 w-8 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
        </svg>
      </div>

      <!-- Title -->
      <div class="text-center">
        <h1 class="text-2xl font-bold text-dark">Merci pour votre commande !</h1>
        <p class="mt-1 text-sm text-brown">Commande {{ order.orderNumber }}</p>
      </div>

      <!-- Order items -->
      <Card class="w-full">
        <h2 class="mb-3 text-base font-semibold text-dark">Votre commande</h2>
        <ul class="flex flex-col gap-2">
          <li
            v-for="(item, index) in order.items"
            :key="index"
            class="flex items-center justify-between text-sm text-dark"
          >
            <span>{{ item.productName }} × {{ item.quantity }}</span>
            <span class="font-medium">{{ formatPrice(item.unitPrice * item.quantity) }}</span>
          </li>
        </ul>
        <div class="mt-3 flex items-center justify-between border-t border-gray-200 pt-3">
          <span class="text-base font-bold text-dark">Total</span>
          <span class="text-base font-bold text-dark">{{ formatPrice(order.totalAmount) }}</span>
        </div>
      </Card>

      <!-- Time slot -->
      <div v-if="order.timeSlot" class="w-full rounded-xl bg-primary px-6 py-4">
        <p class="text-lg font-semibold text-dark">
          {{ formatDate(order.timeSlot.date) }} · {{ formatTime(order.timeSlot.startTime) }} — {{ formatTime(order.timeSlot.endTime) }}
        </p>
        <p class="mt-1 text-sm text-dark">
          Présentez-vous au créneau indiqué pour récupérer votre commande.
        </p>
      </div>

      <!-- Email message -->
      <p class="text-sm text-brown">Un email de confirmation vous a été envoyé.</p>

      <!-- Back to catalog -->
      <Button variant="secondary" class="w-full sm:w-auto" @click="router.push('/')">
        Retour au catalogue
      </Button>
    </div>
  </div>
</template>
