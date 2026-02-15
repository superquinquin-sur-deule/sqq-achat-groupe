import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import { getApiAdminVentesVenteIdSupplierOrders } from '@/api/generated/admin-supplier-orders/admin-supplier-orders'

export function useAdminSupplierOrdersQuery(venteId: MaybeRefOrGetter<number | null>) {
  return useQuery({
    queryKey: computed(() => queryKeys.admin.supplierOrders.list(toValue(venteId)!)),
    queryFn: async () => {
      const response = await getApiAdminVentesVenteIdSupplierOrders(toValue(venteId)!)
      return response.data.data ?? []
    },
    enabled: () => toValue(venteId) !== null,
  })
}
