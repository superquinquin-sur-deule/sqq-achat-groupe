import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery, keepPreviousData } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import { getApiAdminVentesVenteIdPreparation } from '@/api/generated/admin-preparation/admin-preparation'

export function useAdminPreparationQuery(
  venteId: MaybeRefOrGetter<number | null>,
  cursor: MaybeRefOrGetter<string | null> = null,
  timeSlotId: MaybeRefOrGetter<number | null> = null,
) {
  return useQuery({
    queryKey: computed(() =>
      queryKeys.admin.preparation.list(toValue(venteId)!, toValue(cursor), toValue(timeSlotId)),
    ),
    queryFn: async () => {
      const params: Record<string, unknown> = {}
      const c = toValue(cursor)
      if (c) params.cursor = c
      const tsId = toValue(timeSlotId)
      if (tsId !== null && tsId !== undefined) params.timeSlotId = tsId
      const response = await getApiAdminVentesVenteIdPreparation(toValue(venteId)!, params)
      return {
        data: response.data.data ?? [],
        pageInfo: response.data.pageInfo ?? { endCursor: null, hasNext: false },
      }
    },
    enabled: () => toValue(venteId) !== null,
    placeholderData: keepPreviousData,
  })
}
