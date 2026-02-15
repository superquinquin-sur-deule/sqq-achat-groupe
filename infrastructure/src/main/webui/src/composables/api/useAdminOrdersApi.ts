import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import {
  getApiAdminVentesVenteIdOrders,
  getApiAdminVentesVenteIdOrdersId,
  putApiAdminVentesVenteIdOrdersIdPickup,
} from '@/api/generated/admin-orders/admin-orders'

export function useAdminOrdersQuery(
  venteId: MaybeRefOrGetter<number | null>,
  cursor: MaybeRefOrGetter<string | null> = null,
  search: MaybeRefOrGetter<string | null> = null,
  timeSlotId: MaybeRefOrGetter<number | null> = null,
  size: MaybeRefOrGetter<number | null> = null,
) {
  return useQuery({
    queryKey: computed(() =>
      queryKeys.admin.orders.list(
        toValue(venteId)!,
        toValue(cursor),
        toValue(search),
        toValue(timeSlotId),
      ),
    ),
    queryFn: async () => {
      const params: Record<string, unknown> = {}
      const c = toValue(cursor)
      if (c) params.cursor = c
      const s = toValue(search)
      if (s) params.search = s
      const tsId = toValue(timeSlotId)
      if (tsId !== null && tsId !== undefined) params.timeSlotId = tsId
      const sz = toValue(size)
      if (sz !== null && sz !== undefined) params.size = sz
      const response = await getApiAdminVentesVenteIdOrders(toValue(venteId)!, params)
      return {
        data: response.data.data ?? [],
        pageInfo: response.data.pageInfo ?? { endCursor: null, hasNext: false },
      }
    },
    enabled: () => toValue(venteId) !== null,
    placeholderData: keepPreviousData,
  })
}

export function useAdminOrderDetailQuery(
  venteId: MaybeRefOrGetter<number | null>,
  orderId: MaybeRefOrGetter<string>,
) {
  return useQuery({
    queryKey: computed(() => queryKeys.admin.orders.detail(toValue(orderId))),
    queryFn: async () => {
      const response = await getApiAdminVentesVenteIdOrdersId(toValue(venteId)!, toValue(orderId))
      return response.data.data ?? null
    },
    enabled: () => toValue(venteId) !== null,
  })
}

export function usePickupMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (orderId: string) =>
      putApiAdminVentesVenteIdOrdersIdPickup(toValue(venteId)!, orderId),
    onSuccess: () => {
      const id = toValue(venteId)
      if (id !== null) {
        queryClient.invalidateQueries({ queryKey: ['admin', 'orders'] })
      }
    },
  })
}
