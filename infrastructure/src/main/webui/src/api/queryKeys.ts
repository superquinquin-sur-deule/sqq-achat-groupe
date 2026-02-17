export const queryKeys = {
  ventes: {
    all: ['ventes'] as const,
    detail: (venteId: number) => ['ventes', venteId] as const,
    products: (venteId: number) => ['ventes', venteId, 'products'] as const,
    timeslots: (venteId: number) => ['ventes', venteId, 'timeslots'] as const,
  },
  orders: {
    detail: (orderId: string) => ['orders', orderId] as const,
    paymentStatus: (orderId: string) => ['orders', orderId, 'payment-status'] as const,
  },
  admin: {
    ventes: {
      all: ['admin', 'ventes'] as const,
      list: (cursor?: string | null) => ['admin', 'ventes', { cursor }] as const,
      hasOrders: (venteId: number) => ['admin', 'ventes', 'has-orders', { venteId }] as const,
    },
    products: {
      list: (venteId: number, cursor?: string | null) =>
        ['admin', 'products', { venteId, cursor }] as const,
    },
    timeslots: {
      list: (venteId: number, cursor?: string | null) =>
        ['admin', 'timeslots', { venteId, cursor }] as const,
    },
    orders: {
      list: (
        venteId: number,
        cursor?: string | null,
        search?: string | null,
        timeSlotId?: number | null,
      ) => ['admin', 'orders', { venteId, cursor, search, timeSlotId }] as const,
      detail: (orderId: string) => ['admin', 'orders', orderId] as const,
    },
    dashboard: { stats: (venteId: number) => ['admin', 'dashboard', { venteId }] as const },
    preparation: { list: (venteId: number) => ['admin', 'preparation', { venteId }] as const },
    supplierOrders: {
      list: (venteId: number) => ['admin', 'supplier-orders', { venteId }] as const,
    },
  },
}
