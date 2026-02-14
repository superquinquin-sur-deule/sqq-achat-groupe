import { useQuery } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import { getApiVentes, getApiVentesVenteId } from '@/api/generated/ventes/ventes'
import type { MaybeRefOrGetter } from 'vue'
import { toValue } from 'vue'

export function useVentesQuery() {
  return useQuery({
    queryKey: queryKeys.ventes.all,
    queryFn: async () => {
      const response = await getApiVentes()
      return response.data.data ?? []
    },
  })
}

export function useVenteQuery(venteId: MaybeRefOrGetter<number>) {
  return useQuery({
    queryKey: ['ventes', venteId] as const,
    queryFn: async () => {
      const response = await getApiVentesVenteId(toValue(venteId))
      return response.data.data ?? null
    },
  })
}
