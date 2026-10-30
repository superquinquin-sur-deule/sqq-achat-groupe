import { computed } from 'vue'
import { storeToRefs } from 'pinia'
import { useVenteStore } from '@/stores/venteStore'
import { useAdminVentesQuery } from '@/composables/api/useAdminVentesApi'
import { usesTimeSlots } from '@/utils/pickup-formatters'

/**
 * La vente actuellement sélectionnée dans la sidenav admin, et son mode de retrait.
 * Les écrans back-office s'en servent pour masquer tout ce qui touche aux créneaux
 * quand la vente fonctionne en plage de dates.
 */
export function useSelectedVente() {
  const venteStore = useVenteStore()
  const { selectedVenteId } = storeToRefs(venteStore)
  const { data: ventesPage } = useAdminVentesQuery()

  const selectedVente = computed(() =>
    (ventesPage.value?.data ?? []).find((v) => v.id === selectedVenteId.value),
  )
  const venteUsesTimeSlots = computed(() => usesTimeSlots(selectedVente.value))

  return { selectedVenteId, selectedVente, venteUsesTimeSlots }
}
