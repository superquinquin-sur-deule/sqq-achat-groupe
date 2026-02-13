import { api } from './client'

interface InitiatePaymentResponse {
  data: {
    checkoutUrl: string
  }
}

export async function initiatePayment(
  orderId: number,
  successUrl: string,
  cancelUrl: string,
): Promise<{ checkoutUrl: string }> {
  const response = await api.post<InitiatePaymentResponse>(`/api/orders/${orderId}/payment`, {
    successUrl,
    cancelUrl,
  })
  return response.data
}
