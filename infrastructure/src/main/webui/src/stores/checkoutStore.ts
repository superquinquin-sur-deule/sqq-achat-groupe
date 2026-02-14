import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import type { OrderResponse, TimeSlotResponse } from '@/api/generated/model'
import { postApiVentesVenteIdOrders, postApiOrdersIdPayment } from '@/api/generated/orders/orders'
import { getApiVentesVenteIdTimeslots } from '@/api/generated/timeslots/timeslots'
import { useCartStore } from '@/stores/cartStore'

interface CustomerInfo {
  name: string
  email: string
  phone: string
}

export const useCheckoutStore = defineStore('checkout', () => {
  const customerInfo = ref<CustomerInfo>({ name: '', email: '', phone: '' })
  const selectedTimeSlotId = ref<number | null>(null)
  const currentStep = ref(2)
  const isSubmitting = ref(false)
  const isLoadingTimeSlots = ref(false)
  const error = ref<string | null>(null)
  const timeSlots = ref<TimeSlotResponse[]>([])
  const lastOrder = ref<OrderResponse | null>(null)

  const selectedTimeSlot = computed(() =>
    timeSlots.value.find((s) => s.id === selectedTimeSlotId.value) ?? null,
  )

  function setCustomerInfo(info: CustomerInfo) {
    customerInfo.value = info
    currentStep.value = 3
  }

  function selectTimeSlot(slotId: number) {
    selectedTimeSlotId.value = slotId
  }

  async function loadTimeSlots(venteId: number) {
    error.value = null
    isLoadingTimeSlots.value = true
    try {
      const response = await getApiVentesVenteIdTimeslots(venteId)
      timeSlots.value = response.data.data ?? []
    } finally {
      isLoadingTimeSlots.value = false
    }
  }

  async function submitOrder(venteId: number): Promise<OrderResponse> {
    const cartStore = useCartStore()
    error.value = null

    const response = await postApiVentesVenteIdOrders(venteId, {
      customerName: customerInfo.value.name,
      email: customerInfo.value.email,
      phone: customerInfo.value.phone,
      timeSlotId: selectedTimeSlotId.value!,
      items: cartStore.items.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
      })),
    })

    const order = (response.data as { data: OrderResponse }).data
    lastOrder.value = order
    return order
  }

  async function initiatePayment(venteId: number): Promise<void> {
    isSubmitting.value = true
    error.value = null

    try {
      const order = await submitOrder(venteId)

      const baseUrl = window.location.origin
      const successUrl = `${baseUrl}/confirmation?orderId=${order.id}&session_id={CHECKOUT_SESSION_ID}`
      const cancelUrl = `${baseUrl}/payment-error?orderId=${order.id}`

      const paymentResponse = await postApiOrdersIdPayment(String(order.id), { successUrl, cancelUrl })

      window.location.href = (paymentResponse.data as { data: { checkoutUrl: string } }).data.checkoutUrl
    } catch (e: unknown) {
      if (e instanceof Error) {
        error.value = e.message
      }
      throw e
    } finally {
      isSubmitting.value = false
    }
  }

  function reset() {
    customerInfo.value = { name: '', email: '', phone: '' }
    selectedTimeSlotId.value = null
    currentStep.value = 2
    isSubmitting.value = false
    isLoadingTimeSlots.value = false
    error.value = null
    timeSlots.value = []
    lastOrder.value = null
  }

  return {
    customerInfo,
    selectedTimeSlotId,
    selectedTimeSlot,
    currentStep,
    isSubmitting,
    isLoadingTimeSlots,
    error,
    timeSlots,
    lastOrder,
    setCustomerInfo,
    selectTimeSlot,
    loadTimeSlots,
    submitOrder,
    initiatePayment,
    reset,
  }
})
