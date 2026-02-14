import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import { getApiVentesVenteIdProducts } from '@/api/generated/products/products'

export function useVenteProductsQuery(
  venteId: MaybeRefOrGetter<number>,
  enabled: MaybeRefOrGetter<boolean> = true,
) {
  return useQuery({
    queryKey: computed(() => queryKeys.ventes.products(toValue(venteId))),
    queryFn: async () => {
      const response = await getApiVentesVenteIdProducts(toValue(venteId))
      return response.data.data ?? []
    },
    enabled: () => toValue(enabled),
  })
}
