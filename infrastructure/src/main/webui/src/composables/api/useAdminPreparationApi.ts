import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import { getApiAdminPreparation } from '@/api/generated/admin-preparation/admin-preparation'

export function useAdminPreparationQuery(venteId: MaybeRefOrGetter<number | null>) {
  return useQuery({
    queryKey: computed(() => queryKeys.admin.preparation.list(toValue(venteId)!)),
    queryFn: async () => {
      const response = await getApiAdminPreparation({ venteId: toValue(venteId)! })
      return response.data.data ?? []
    },
    enabled: () => toValue(venteId) !== null,
  })
}
