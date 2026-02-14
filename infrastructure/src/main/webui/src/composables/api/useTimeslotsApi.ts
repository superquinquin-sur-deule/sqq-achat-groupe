import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import { getApiVentesVenteIdTimeslots } from '@/api/generated/timeslots/timeslots'

export function useVenteTimeslotsQuery(venteId: MaybeRefOrGetter<number>) {
  return useQuery({
    queryKey: computed(() => queryKeys.ventes.timeslots(toValue(venteId))),
    queryFn: async () => {
      const response = await getApiVentesVenteIdTimeslots(toValue(venteId))
      return response.data.data ?? []
    },
  })
}
