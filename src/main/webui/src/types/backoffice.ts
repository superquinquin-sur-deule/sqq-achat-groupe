export interface BackofficeOrder {
  id: number
  orderNumber: string
  customerName: string
  customerEmail: string
  timeSlotLabel: string
  totalAmount: number
  status: string
  createdAt: string
}

export interface BackofficeOrderDetail {
  id: number
  orderNumber: string
  status: string
  customerName: string
  customerEmail: string
  customerPhone: string
  timeSlot: {
    date: string
    startTime: string
    endTime: string
  } | null
  items: BackofficeOrderItem[]
  totalAmount: number
  createdAt: string
}

export interface BackofficeOrderItem {
  productName: string
  quantity: number
  unitPrice: number
  subtotal: number
}

export interface SupplierOrderLine {
  productName: string
  supplier: string
  totalQuantity: number
}

export interface PreparationOrder {
  orderId: number
  orderNumber: string
  customerName: string
  timeSlotLabel: string
  items: PreparationItem[]
}

export interface PreparationItem {
  productName: string
  quantity: number
}
