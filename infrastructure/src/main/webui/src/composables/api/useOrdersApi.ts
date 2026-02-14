import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import { getApiOrdersId, getApiOrdersIdPaymentStatus } from '@/api/generated/orders/orders'

export function useOrderDetailQuery(
  orderId: MaybeRefOrGetter<string>,
  enabled: MaybeRefOrGetter<boolean> = true,
) {
  return useQuery({
    queryKey: computed(() => queryKeys.orders.detail(toValue(orderId))),
    queryFn: async () => {
      const response = await getApiOrdersId(toValue(orderId))
      return response.data.data ?? null
    },
    enabled: () => toValue(enabled),
  })
}

export function usePaymentStatusQuery(
  orderId: MaybeRefOrGetter<string>,
  enabled: MaybeRefOrGetter<boolean> = true,
) {
  return useQuery({
    queryKey: computed(() => queryKeys.orders.paymentStatus(toValue(orderId))),
    queryFn: async () => {
      const response = await getApiOrdersIdPaymentStatus(toValue(orderId))
      return response.data.data ?? null
    },
    enabled: () => toValue(enabled),
  })
}
