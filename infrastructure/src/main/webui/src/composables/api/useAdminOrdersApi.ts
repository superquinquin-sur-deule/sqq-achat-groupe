import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import {
  getApiAdminOrders,
  getApiAdminOrdersId,
  putApiAdminOrdersIdPickup,
} from '@/api/generated/admin-orders/admin-orders'

export function useAdminOrdersQuery(venteId: MaybeRefOrGetter<number | null>) {
  return useQuery({
    queryKey: computed(() => queryKeys.admin.orders.list(toValue(venteId)!)),
    queryFn: async () => {
      const response = await getApiAdminOrders({ venteId: toValue(venteId)! })
      return response.data.data ?? []
    },
    enabled: () => toValue(venteId) !== null,
  })
}

export function useAdminOrderDetailQuery(orderId: MaybeRefOrGetter<string>) {
  return useQuery({
    queryKey: computed(() => queryKeys.admin.orders.detail(toValue(orderId))),
    queryFn: async () => {
      const response = await getApiAdminOrdersId(toValue(orderId))
      return response.data.data ?? null
    },
  })
}

export function usePickupMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (orderId: string) => putApiAdminOrdersIdPickup(orderId),
    onSuccess: () => {
      const id = toValue(venteId)
      if (id !== null) {
        queryClient.invalidateQueries({ queryKey: queryKeys.admin.orders.list(id) })
      }
    },
  })
}
