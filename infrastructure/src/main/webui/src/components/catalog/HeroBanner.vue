<script setup lang="ts">
import { computed } from 'vue'
import type { VenteResponse, TimeSlotResponse } from '@/api/generated/model'
import { formatPickupRange, usesTimeSlots } from '@/utils/pickup-formatters'

const props = defineProps<{
  vente: VenteResponse | null
  timeSlots: TimeSlotResponse[]
  timeSlotsLoading: boolean
}>()

function formatDateFr(dateStr: string): string {
  const date = new Date(dateStr)
  return date.toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
  })
}

function formatSlotDate(dateStr: string): string {
  const date = new Date(dateStr + 'T00:00:00')
  return date.toLocaleDateString('fr-FR', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
  })
}

const orderPeriod = computed(() => {
  if (!props.vente) return ''
  return `COMMANDE DU ${formatDateFr(props.vente.startDate).toUpperCase()} AU ${formatDateFr(props.vente.endDate).toUpperCase()}`
})

const uniqueSlotDates = computed(() => {
  const seen = new Set<string>()
  const dates: string[] = []
  for (const slot of props.timeSlots) {
    if (!seen.has(slot.date)) {
      seen.add(slot.date)
      dates.push(slot.date)
    }
  }
  return dates
})

const formattedSlotDates = computed(() => {
  return uniqueSlotDates.value.map((d, i) => {
    const formatted = formatSlotDate(d)
    if (i === 0) return `le ${formatted}`
    return `${formatted}`
  })
})

// Vente sans créneau : le retrait se fait librement sur la plage configurée.
const venteUsesTimeSlots = computed(() => usesTimeSlots(props.vente))
const pickupRangeText = computed(() =>
  formatPickupRange(props.vente?.pickupStartDate, props.vente?.pickupEndDate, false),
)

const slotDatesText = computed(() => {
  if (formattedSlotDates.value.length === 0) return ''
  if (formattedSlotDates.value.length === 1) return formattedSlotDates.value[0]
  const last = formattedSlotDates.value[formattedSlotDates.value.length - 1]
  const rest = formattedSlotDates.value.slice(0, -1)
  return rest.join(', ') + ' et ' + last
})
</script>

<template>
  <div data-testid="hero-banner" class="relative overflow-hidden bg-white">
    <div
      class="absolute inset-0 bg-cover bg-center opacity-40"
      :style="{ backgroundImage: `url('/bg-pattern.png')` }"
    />
    <div class="relative mx-auto max-w-5xl px-4 py-8 md:py-12">
      <div class="flex flex-col items-center text-center">
        <!-- Titre en texte réel : éditable, sélectionnable et lu par les lecteurs d'écran
             (l'ancien bandeau était une image dont le sous-titre n'apparaissait pas dans le alt). -->
        <h1 class="-rotate-[3deg]">
          <span
            class="inline-block rounded-2xl bg-primary px-6 py-2 font-display text-4xl leading-tight tracking-wide text-brown sm:px-10 sm:py-3 sm:text-6xl md:text-7xl"
          >
            <!-- Le cadre reste un rectangle (simplement tourné) ; seules les lettres sont obliques. -->
            <span class="inline-block" style="transform: skewX(-8deg)">PRÉ-COMMANDES</span>
          </span>
        </h1>
        <p
          class="mt-5 max-w-md -rotate-[3deg] text-balance text-lg text-brown sm:mt-6 sm:text-2xl"
          data-testid="hero-subtitle"
        >
          Réserve tes produits SuperQuinquin
        </p>
      </div>

      <div
        v-if="vente"
        data-testid="vente-info-card"
        class="mx-auto mt-6 max-w-xl rounded-2xl border-4 border-primary bg-white p-6 text-center shadow-sm"
      >
        <p class="text-sm font-bold uppercase tracking-wide text-dark">
          {{ orderPeriod }}
        </p>
        <p class="mt-4 text-sm uppercase">Récupère-la :</p>

        <template v-if="!venteUsesTimeSlots">
          <p
            v-if="pickupRangeText"
            class="mt-2 text-sm font-bold text-dark first-letter:uppercase"
            data-testid="hero-pickup-range"
          >
            {{ pickupRangeText }}
          </p>
          <p class="mt-1 text-sm text-dark">au magasin, sans réservation de créneau</p>
        </template>

        <div v-else-if="timeSlotsLoading" class="mt-2 text-sm text-gray-500">
          Chargement des créneaux...
        </div>
        <template v-else-if="timeSlots.length > 0">
          <p class="mt-2 text-sm font-bold text-dark first-letter:uppercase">
            {{ slotDatesText }}
          </p>
          <p class="mt-1 text-sm text-dark">
            au magasin sur réservation de créneaux au moment de ton achat
          </p>
        </template>
      </div>
    </div>
  </div>
</template>
