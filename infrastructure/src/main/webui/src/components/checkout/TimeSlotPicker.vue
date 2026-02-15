<script setup lang="ts">
import { computed } from 'vue'
import { cva } from 'class-variance-authority'
import type { TimeSlotResponse } from '@/api/generated/model'
import Button from '@/components/ui/Button.vue'

const props = defineProps<{
  timeSlots: TimeSlotResponse[]
  selectedId: number | null
  isLoading?: boolean
}>()

const emit = defineEmits<{
  select: [slotId: number]
  continue: []
  back: []
}>()

const slotVariants = cva(
  'min-h-[44px] w-full rounded-lg border px-4 py-3 text-left text-sm font-medium transition-colors focus:outline-2 focus:outline-offset-2 focus:outline-dark',
  {
    variants: {
      state: {
        selected: 'border-primary bg-primary text-dark',
        available: 'border-gray-300 bg-white text-dark hover:border-primary',
        disabled: 'cursor-not-allowed border-gray-200 bg-gray-100 text-gray-400',
      },
    },
    defaultVariants: { state: 'available' },
  },
)

function getSlotState(slot: TimeSlotResponse): 'selected' | 'available' | 'disabled' {
  if (props.selectedId === slot.id) return 'selected'
  if (slot.remainingPlaces <= 0) return 'disabled'
  return 'available'
}

const groupedByDate = computed(() => {
  const groups: { date: string; formattedDate: string; slots: TimeSlotResponse[] }[] = []
  const map = new Map<string, TimeSlotResponse[]>()

  for (const slot of props.timeSlots) {
    let arr = map.get(slot.date)
    if (!arr) {
      arr = []
      map.set(slot.date, arr)
      groups.push({ date: slot.date, formattedDate: formatDate(slot.date), slots: arr })
    }
    arr.push(slot)
  }

  return groups
})

function formatTime(time: string): string {
  return time.substring(0, 5).replace(':', 'h')
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr + 'T00:00:00')
  return date.toLocaleDateString('fr-FR', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}
</script>

<template>
  <div class="mx-auto flex w-full max-w-[480px] flex-col gap-4">
    <h2 class="text-lg font-semibold text-dark">Choisissez votre créneau de retrait</h2>

    <div v-if="isLoading" class="flex items-center justify-center py-8">
      <p class="text-sm text-gray-500">Chargement des créneaux...</p>
    </div>

    <template v-else-if="timeSlots.length > 0">
      <div v-for="group in groupedByDate" :key="group.date" class="flex flex-col gap-2">
        <h3 class="text-base font-medium text-dark">{{ group.formattedDate }}</h3>

        <div
          role="radiogroup"
          :aria-label="`Créneaux du ${group.formattedDate}`"
          class="flex flex-col gap-2"
        >
          <button
            v-for="slot in group.slots"
            :key="slot.id"
            role="radio"
            :aria-checked="selectedId === slot.id"
            :aria-disabled="slot.remainingPlaces <= 0"
            :disabled="slot.remainingPlaces <= 0"
            :class="slotVariants({ state: getSlotState(slot) })"
            @click="slot.remainingPlaces > 0 && emit('select', slot.id)"
          >
            <span v-if="slot.remainingPlaces > 0">
              {{ formatTime(slot.startTime) }} — {{ formatTime(slot.endTime) }} ·
              {{ slot.remainingPlaces }} places
            </span>
            <span v-else>
              {{ formatTime(slot.startTime) }} — {{ formatTime(slot.endTime) }} · Complet
            </span>
          </button>
        </div>
      </div>
    </template>

    <p v-else class="text-sm text-gray-500">Aucun créneau disponible pour le moment.</p>

    <div class="mt-4 flex flex-col gap-2 sm:flex-row-reverse">
      <Button
        variant="primary"
        :disabled="selectedId === null"
        class="w-full sm:w-auto sm:min-w-[200px]"
        @click="emit('continue')"
      >
        Continuer
      </Button>
      <Button variant="secondary" class="w-full sm:w-auto" @click="emit('back')"> Retour </Button>
    </div>
  </div>
</template>
