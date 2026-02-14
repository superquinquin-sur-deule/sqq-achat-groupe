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
    ventes: { all: ['admin', 'ventes'] as const },
    products: { list: (venteId: number) => ['admin', 'products', { venteId }] as const },
    timeslots: { list: (venteId: number) => ['admin', 'timeslots', { venteId }] as const },
    orders: {
      list: (venteId: number) => ['admin', 'orders', { venteId }] as const,
      detail: (orderId: string) => ['admin', 'orders', orderId] as const,
    },
    dashboard: { stats: (venteId: number) => ['admin', 'dashboard', { venteId }] as const },
    preparation: { list: (venteId: number) => ['admin', 'preparation', { venteId }] as const },
    supplierOrders: {
      list: (venteId: number) => ['admin', 'supplier-orders', { venteId }] as const,
    },
  },
}
