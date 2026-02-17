<script setup lang="ts">
import { computed } from 'vue'
import type { TimeSlotResponse } from '@/api/generated/model'

const props = defineProps<{
  timeSlots: TimeSlotResponse[]
  isLoading: boolean
}>()

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
</script>

<template>
  <div data-testid="timeslot-banner" class="bg-white px-4 py-6 md:px-6 md:py-8">
    <div class="mx-auto max-w-5xl">
      <div class="flex items-start gap-3">
        <svg
          class="mt-0.5 h-6 w-6 shrink-0 text-dark"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 0 1 2.25-2.25h13.5A2.25 2.25 0 0 1 21 7.5v11.25m-18 0A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75m-18 0v-7.5A2.25 2.25 0 0 1 5.25 9h13.5A2.25 2.25 0 0 1 21 11.25v7.5"
          />
        </svg>
        <div>
          <h2 class="text-lg font-semibold text-dark">Créneaux de retrait</h2>

          <div v-if="isLoading" class="mt-2 text-sm text-gray-500">Chargement des créneaux...</div>

          <p v-else-if="timeSlots.length === 0" class="mt-2 text-sm text-gray-500">
            Aucun créneau disponible pour le moment.
          </p>

          <div v-else class="mt-2 space-y-1">
            <p
              v-for="group in groupedByDate"
              :key="group.date"
              class="text-sm text-dark"
            >
              <span class="font-medium capitalize">{{ group.formattedDate }}</span> :
              {{ group.slots.map((s) => `${formatTime(s.startTime)} — ${formatTime(s.endTime)}`).join(', ') }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
