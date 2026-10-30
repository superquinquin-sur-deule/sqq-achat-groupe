<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cartStore'
import { useCheckoutStore } from '@/stores/checkoutStore'
import { useToast } from '@/composables/useToast'
import { useVenteTimeslotsQuery } from '@/composables/api/useTimeslotsApi'
import { useVenteExpiredRedirect } from '@/composables/useVenteExpiredRedirect'
import { useCheckoutSteps } from '@/composables/useCheckoutSteps'
import { ApiError } from '@/api/mutator/custom-fetch'
import Stepper from '@/components/ui/Stepper.vue'
import CheckoutForm from '@/components/checkout/CheckoutForm.vue'
import TimeSlotPicker from '@/components/checkout/TimeSlotPicker.vue'
import OrderSummary from '@/components/checkout/OrderSummary.vue'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const checkoutStore = useCheckoutStore()
const toast = useToast()
const venteId = Number(route.params.venteId)

useVenteExpiredRedirect(venteId)

// Vente en plage de dates : aucun créneau à choisir, l'étape 3 disparaît du tunnel.
const { vente, venteUsesTimeSlots, steps } = useCheckoutSteps(venteId)

const { data: timeSlots, isLoading: isLoadingTimeSlots } = useVenteTimeslotsQuery(venteId)

const selectedTimeSlot = computed(
  () => (timeSlots.value ?? []).find((s) => s.id === checkoutStore.selectedTimeSlotId) ?? null,
)

// Le store conserve ses numéros d'étape (2 = coordonnées, 4 = récapitulatif) ;
// seul l'affichage est compacté quand l'étape créneau n'existe pas.
const displayStep = computed(() =>
  venteUsesTimeSlots.value
    ? checkoutStore.currentStep
    : checkoutStore.currentStep === 4
      ? 3
      : checkoutStore.currentStep,
)

onMounted(() => {
  if (cartStore.isEmpty) {
    router.replace({ name: 'home', params: { venteId } })
    return
  }
  checkoutStore.reset()
})

function handleCustomerSubmit(info: {
  firstName: string
  lastName: string
  email: string
  phone: string
}) {
  checkoutStore.setCustomerInfo(info, !venteUsesTimeSlots.value)
}

function handleTimeSlotContinue() {
  if (checkoutStore.selectedTimeSlotId !== null) {
    checkoutStore.currentStep = 4
  }
}

function handleSummaryBack() {
  checkoutStore.currentStep = venteUsesTimeSlots.value ? 3 : 2
}

async function handleConfirm() {
  try {
    const order = await checkoutStore.confirmOrder(venteId)
    router.push({ name: 'confirmation', query: { orderId: String(order.id) } })
  } catch (e: unknown) {
    if (e instanceof ApiError) {
      toast.error(e.message)
    } else if (e instanceof Error) {
      toast.error(e.message)
    }
  }
}
</script>

<template>
  <div class="mx-auto max-w-7xl px-4 py-8">
    <h1 class="mb-6 text-2xl font-bold text-dark">Commande</h1>

    <Stepper :steps="steps" :current-step="displayStep" class="mb-8" />

    <CheckoutForm
      v-if="checkoutStore.currentStep === 2"
      @submit="handleCustomerSubmit"
      @back="router.push({ name: 'cart', params: { venteId } })"
    />

    <TimeSlotPicker
      v-else-if="checkoutStore.currentStep === 3 && venteUsesTimeSlots"
      :time-slots="timeSlots ?? []"
      :selected-id="checkoutStore.selectedTimeSlotId"
      :is-loading="isLoadingTimeSlots"
      @select="checkoutStore.selectTimeSlot"
      @continue="handleTimeSlotContinue"
      @back="checkoutStore.currentStep = 2"
    />

    <OrderSummary
      v-else-if="checkoutStore.currentStep === 4 && (selectedTimeSlot || !venteUsesTimeSlots)"
      :customer-info="checkoutStore.customerInfo"
      :time-slot="selectedTimeSlot"
      :pickup-start-date="vente?.pickupStartDate ?? null"
      :pickup-end-date="vente?.pickupEndDate ?? null"
      :items="cartStore.items"
      :total="cartStore.total"
      :is-submitting="checkoutStore.isSubmitting"
      @confirm="handleConfirm"
      @back="handleSummaryBack"
    />
  </div>
</template>
