import { api } from './client'
import type { CreateOrderRequest, OrderResponse, OrderDetailResponse, PaymentStatusResponse } from '@/types/order'

interface CreateOrderResponse {
  data: OrderResponse
}

interface GetOrderResponse {
  data: OrderDetailResponse
}

interface GetPaymentStatusResponse {
  data: PaymentStatusResponse
}

export async function createOrder(venteId: number, request: CreateOrderRequest): Promise<OrderResponse> {
  const response = await api.post<CreateOrderResponse>(`/api/ventes/${venteId}/orders`, request)
  return response.data
}

export async function fetchOrder(orderId: number): Promise<OrderDetailResponse> {
  const response = await api.get<GetOrderResponse>(`/api/orders/${orderId}`)
  return response.data
}

export async function fetchPaymentStatus(orderId: number): Promise<PaymentStatusResponse> {
  const response = await api.get<GetPaymentStatusResponse>(`/api/orders/${orderId}/payment-status`)
  return response.data
}
