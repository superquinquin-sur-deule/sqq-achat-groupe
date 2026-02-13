export interface SlotDistribution {
  slotId: number
  slotLabel: string
  orderCount: number
}

export interface DashboardStats {
  totalOrders: number
  totalAmount: number
  pickupRate: number
  averageBasket: number
  slotDistribution: SlotDistribution[]
}
