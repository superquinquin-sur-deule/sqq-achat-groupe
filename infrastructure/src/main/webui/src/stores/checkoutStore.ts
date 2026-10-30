import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { OrderResponse } from '@/api/generated/model'
import { postApiVentesVenteIdOrders } from '@/api/generated/orders/orders'
import { useCartStore } from '@/stores/cartStore'

interface CustomerInfo {
  firstName: string
  lastName: string
  email: string
  phone: string
}

export const useCheckoutStore = defineStore('checkout', () => {
  const customerInfo = ref<CustomerInfo>({ firstName: '', lastName: '', email: '', phone: '' })
  const selectedTimeSlotId = ref<number | null>(null)
  const currentStep = ref(2)
  const isSubmitting = ref(false)
  const error = ref<string | null>(null)
  const lastOrder = ref<OrderResponse | null>(null)
  const idempotencyKey = ref(crypto.randomUUID())

  function setCustomerInfo(info: CustomerInfo) {
    customerInfo.value = info
    currentStep.value = 3
  }

  function selectTimeSlot(slotId: number) {
    selectedTimeSlotId.value = slotId
  }

  async function submitOrder(venteId: number): Promise<OrderResponse> {
    const cartStore = useCartStore()
    error.value = null

    const response = await postApiVentesVenteIdOrders(venteId, {
      customerFirstName: customerInfo.value.firstName,
      customerLastName: customerInfo.value.lastName,
      email: customerInfo.value.email,
      phone: customerInfo.value.phone,
      timeSlotId: selectedTimeSlotId.value!,
      idempotencyKey: idempotencyKey.value,
      items: cartStore.items.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
      })),
    })

    const order = (response.data as { data: OrderResponse }).data
    lastOrder.value = order
    return order
  }

  let pendingConfirmation: Promise<OrderResponse> | null = null

  async function confirmOrder(venteId: number): Promise<OrderResponse> {
    if (pendingConfirmation) return pendingConfirmation

    isSubmitting.value = true
    error.value = null

    pendingConfirmation = submitOrder(venteId)
      .catch((e: unknown) => {
        if (e instanceof Error) {
          error.value = e.message
        }
        throw e
      })
      .finally(() => {
        isSubmitting.value = false
        pendingConfirmation = null
      })

    return pendingConfirmation
  }

  function reset() {
    customerInfo.value = { firstName: '', lastName: '', email: '', phone: '' }
    selectedTimeSlotId.value = null
    currentStep.value = 2
    isSubmitting.value = false
    error.value = null
    lastOrder.value = null
    idempotencyKey.value = crypto.randomUUID()
  }

  return {
    customerInfo,
    selectedTimeSlotId,
    currentStep,
    isSubmitting,
    error,
    lastOrder,
    idempotencyKey,
    setCustomerInfo,
    selectTimeSlot,
    submitOrder,
    confirmOrder,
    reset,
  }
})
