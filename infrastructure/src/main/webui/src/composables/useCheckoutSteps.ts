import { computed } from 'vue'
import { useVenteQuery } from '@/composables/api/useVentesApi'
import { usesTimeSlots } from '@/utils/pickup-formatters'

/**
 * Les étapes du tunnel de commande, partagées entre le panier et le checkout.
 * Une vente en plage de dates n'a pas d'étape « Créneau » : le stepper passe de 4 à 3 étapes,
 * et les deux écrans doivent rester cohérents.
 */
export function useCheckoutSteps(venteId: number) {
  const { data: vente } = useVenteQuery(venteId)
  const venteUsesTimeSlots = computed(() => usesTimeSlots(vente.value))

  const steps = computed(() =>
    venteUsesTimeSlots.value
      ? [
          { label: 'Panier' },
          { label: 'Coordonnées' },
          { label: 'Créneau' },
          { label: 'Récapitulatif' },
        ]
      : [{ label: 'Panier' }, { label: 'Coordonnées' }, { label: 'Récapitulatif' }],
  )

  return { vente, venteUsesTimeSlots, steps }
}
