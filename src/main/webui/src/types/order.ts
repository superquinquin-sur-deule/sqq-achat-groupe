export interface CustomerInfo {
  name: string
  email: string
  phone: string
}

export interface OrderItemRequest {
  productId: number
  quantity: number
}

export interface CreateOrderRequest {
  customerName: string
  email: string
  phone: string
  timeSlotId: number
  items: OrderItemRequest[]
}

export interface OrderResponse {
  id: number
  orderNumber: string
  status: string
  totalAmount: number
  createdAt: string
}

export interface OrderDetailResponse {
  id: number
  orderNumber: string
  status: string
  totalAmount: number
  customerName: string
  customerEmail: string
  timeSlot: {
    date: string
    startTime: string
    endTime: string
  } | null
  items: {
    productName: string
    quantity: number
    unitPrice: number
  }[]
  createdAt: string
}

export interface PaymentStatusResponse {
  attempts: number
  maxAttempts: number
  paymentStatus: string
  orderStatus: string
  canRetry: boolean
}
