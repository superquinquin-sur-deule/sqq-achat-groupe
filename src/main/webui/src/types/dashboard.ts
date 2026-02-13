export interface SlotDistribution {
  slotId: number
  slotLabel: string
  orderCount: number
}

export interface TopProduct {
  productName: string
  totalQuantity: number
}

export interface DashboardStats {
  totalOrders: number
  totalAmount: number
  pickupRate: number
  averageBasket: number
  slotDistribution: SlotDistribution[]
  topProducts: TopProduct[]
}
