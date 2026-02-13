<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cartStore'
import { useCheckoutStore } from '@/stores/checkoutStore'
import { useToast } from '@/composables/useToast'
import { ApiError } from '@/api/client'
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

const steps = [
  { label: 'Panier' },
  { label: 'Coordonnées' },
  { label: 'Créneau' },
  { label: 'Récapitulatif' },
]

onMounted(async () => {
  if (cartStore.isEmpty) {
    router.replace(`/ventes/${venteId}`)
    return
  }
  checkoutStore.reset()
  try {
    await checkoutStore.loadTimeSlots(venteId)
  } catch (e: unknown) {
    if (e instanceof ApiError) {
      toast.error(e.message)
    } else if (e instanceof Error) {
      toast.error('Impossible de charger les créneaux de retrait.')
    }
  }
})

function handleCustomerSubmit(info: { name: string; email: string; phone: string }) {
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
      @back="router.push(`/ventes/${venteId}/cart`)"
    />

    <TimeSlotPicker
      v-else-if="checkoutStore.currentStep === 3"
      :time-slots="checkoutStore.timeSlots"
      :selected-id="checkoutStore.selectedTimeSlotId"
      :is-loading="checkoutStore.isLoadingTimeSlots"
      @select="checkoutStore.selectTimeSlot"
      @continue="handleTimeSlotContinue"
      @back="checkoutStore.currentStep = 2"
    />

    <OrderSummary
      v-else-if="checkoutStore.currentStep === 4 && checkoutStore.selectedTimeSlot"
      :customer-info="checkoutStore.customerInfo"
      :time-slot="checkoutStore.selectedTimeSlot"
      :items="cartStore.items"
      :total="cartStore.total"
      :is-submitting="checkoutStore.isSubmitting"
      @confirm="handleConfirm"
      @back="checkoutStore.currentStep = 3"
    />
  </div>
</template>
