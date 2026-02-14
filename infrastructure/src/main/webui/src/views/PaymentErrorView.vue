<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useOrderDetailQuery, usePaymentStatusQuery } from '@/composables/api/useOrdersApi'
import { postApiOrdersIdPayment, getApiOrdersIdPaymentStatus } from '@/api/generated/orders/orders'
import Card from '@/components/ui/Card.vue'
import Button from '@/components/ui/Button.vue'

const route = useRoute()
const router = useRouter()
const isRetrying = ref(false)

const orderId = route.query.orderId as string | undefined
if (!orderId) {
  router.replace('/')
}

const { data: order, isLoading: isLoadingOrder } = useOrderDetailQuery(orderId ?? '', !!orderId)
const { data: paymentStatus, isLoading: isLoadingStatus } = usePaymentStatusQuery(
  orderId ?? '',
  !!orderId,
)

const isLoading = computed(() => isLoadingOrder.value || isLoadingStatus.value)

const isCancelled = computed(() => {
  if (!paymentStatus.value) return false
  return paymentStatus.value.orderStatus === 'CANCELLED' || !paymentStatus.value.canRetry
})

watch([order, paymentStatus], () => {
  if (!order.value && !isLoading.value && orderId) {
    router.replace('/')
  }
})

function formatPrice(price: number): string {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(price)
}

function formatTime(time: string): string {
  return time.substring(0, 5).replace(':', 'h')
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr + 'T00:00:00')
  return date.toLocaleDateString('fr-FR', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}

async function retryPayment() {
  if (!order.value) return
  isRetrying.value = true

  try {
    const baseUrl = window.location.origin
    const successUrl = `${baseUrl}/confirmation?orderId=${order.value.id}&session_id={CHECKOUT_SESSION_ID}`
    const cancelUrl = `${baseUrl}/payment-error?orderId=${order.value.id}`

    const paymentResponse = await postApiOrdersIdPayment(order.value.id, { successUrl, cancelUrl })
    window.location.href = (
      paymentResponse.data as { data: { checkoutUrl: string } }
    ).data.checkoutUrl
  } catch {
    isRetrying.value = false
    if (orderId) {
      const statusResponse = await getApiOrdersIdPaymentStatus(orderId)
      // Force a local update — paymentStatus from query won't auto-update since we're not invalidating
      paymentStatus.value = statusResponse.data.data ?? null
    }
  }
}
</script>

<template>
  <main class="mx-auto max-w-2xl px-4 py-8 md:px-6">
    <!-- Loading -->
    <div v-if="isLoading" class="flex justify-center py-12">
      <svg
        class="h-8 w-8 animate-spin text-dark"
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        aria-label="Chargement"
      >
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
        <path
          class="opacity-75"
          fill="currentColor"
          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
        />
      </svg>
    </div>

    <!-- Cancellation mode -->
    <div
      v-else-if="isCancelled"
      class="flex flex-col items-center gap-6"
      data-testid="payment-cancelled"
    >
      <!-- X circle icon -->
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
            d="M9.75 9.75l4.5 4.5m0-4.5l-4.5 4.5M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
      </div>

      <div class="text-center">
        <h1 class="text-2xl font-bold text-dark">Votre commande a été annulée</h1>
        <p role="alert" class="mt-2 text-brown" data-testid="cancellation-message">
          Vous pouvez recommencer quand vous le souhaitez.
        </p>
      </div>

      <Button class="w-full sm:w-auto sm:min-w-[200px]" @click="router.push('/')">
        Retour au catalogue
      </Button>
    </div>

    <!-- Retry mode -->
    <div
      v-else-if="order && paymentStatus"
      class="flex flex-col items-center gap-6"
      data-testid="payment-error"
    >
      <!-- Warning icon -->
      <div class="flex h-16 w-16 items-center justify-center rounded-full bg-orange-400">
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
        <h1 class="text-2xl font-bold text-dark">Le paiement n'a pas fonctionné</h1>
        <p role="alert" class="mt-2 text-brown" data-testid="error-message">
          Pas d'inquiétude, vos produits sont encore réservés.
        </p>
      </div>

      <!-- Order summary -->
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
          {{ formatDate(order.timeSlot.date) }} · {{ formatTime(order.timeSlot.startTime) }} —
          {{ formatTime(order.timeSlot.endTime) }}
        </p>
      </div>

      <!-- Attempt indicator -->
      <p class="text-sm text-brown">
        Tentative {{ paymentStatus.attempts }}/{{ paymentStatus.maxAttempts }}
      </p>

      <!-- Retry button -->
      <Button
        class="w-full sm:w-auto sm:min-w-[200px]"
        :loading="isRetrying"
        data-testid="retry-button"
        @click="retryPayment"
      >
        {{ isRetrying ? 'Redirection vers le paiement...' : 'Réessayer le paiement' }}
      </Button>

      <Button variant="secondary" class="w-full sm:w-auto" @click="router.push('/')">
        Retour au catalogue
      </Button>
    </div>
  </main>
</template>
