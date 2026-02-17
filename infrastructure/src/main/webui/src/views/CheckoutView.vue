<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cartStore'
import { useCheckoutStore } from '@/stores/checkoutStore'
import { useToast } from '@/composables/useToast'
import { useVenteTimeslotsQuery } from '@/composables/api/useTimeslotsApi'
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

const { data: timeSlots, isLoading: isLoadingTimeSlots } = useVenteTimeslotsQuery(venteId)

const selectedTimeSlot = computed(
  () => (timeSlots.value ?? []).find((s) => s.id === checkoutStore.selectedTimeSlotId) ?? null,
)

const steps = [
  { label: 'Panier' },
  { label: 'Coordonnées' },
  { label: 'Créneau' },
  { label: 'Récapitulatif' },
]

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
  checkoutStore.setCustomerInfo(info)
}

function handleTimeSlotContinue() {
  if (checkoutStore.selectedTimeSlotId !== null) {
    checkoutStore.currentStep = 4
  }
}

async function handleConfirm() {
  try {
    await checkoutStore.initiatePayment(venteId)
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

    <Stepper :steps="steps" :current-step="checkoutStore.currentStep" class="mb-8" />

    <CheckoutForm
      v-if="checkoutStore.currentStep === 2"
      @submit="handleCustomerSubmit"
      @back="router.push({ name: 'cart', params: { venteId } })"
    />

    <TimeSlotPicker
      v-else-if="checkoutStore.currentStep === 3"
      :time-slots="timeSlots ?? []"
      :selected-id="checkoutStore.selectedTimeSlotId"
      :is-loading="isLoadingTimeSlots"
      @select="checkoutStore.selectTimeSlot"
      @continue="handleTimeSlotContinue"
      @back="checkoutStore.currentStep = 2"
    />

    <OrderSummary
      v-else-if="checkoutStore.currentStep === 4 && selectedTimeSlot"
      :customer-info="checkoutStore.customerInfo"
      :time-slot="selectedTimeSlot"
      :items="cartStore.items"
      :total="cartStore.total"
      :is-submitting="checkoutStore.isSubmitting"
      @confirm="handleConfirm"
      @back="checkoutStore.currentStep = 3"
    />
  </div>
</template>
