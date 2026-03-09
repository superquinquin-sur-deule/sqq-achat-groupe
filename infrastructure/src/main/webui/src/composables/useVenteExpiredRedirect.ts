import { watch } from 'vue'
import { useRouter } from 'vue-router'
import { useVenteQuery } from '@/composables/api/useVentesApi'

export function useVenteExpiredRedirect(venteId: number) {
  const router = useRouter()
  const { data: vente } = useVenteQuery(venteId)

  watch(
    vente,
    (v) => {
      if (v?.endDate && new Date(v.endDate).getTime() < Date.now()) {
        router.replace({ name: 'ventes' })
      }
    },
    { immediate: true },
  )
}
