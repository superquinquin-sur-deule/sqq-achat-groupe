import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import { getApiAdminVentesVenteIdDashboard } from '@/api/generated/admin-dashboard/admin-dashboard'

export function useAdminDashboardQuery(venteId: MaybeRefOrGetter<number | null>) {
  return useQuery({
    queryKey: computed(() => queryKeys.admin.dashboard.stats(toValue(venteId)!)),
    queryFn: async () => {
      const response = await getApiAdminVentesVenteIdDashboard(toValue(venteId)!)
      return response.data.data ?? null
    },
    enabled: () => toValue(venteId) !== null,
  })
}
