<script setup lang="ts">
import { computed } from 'vue'

interface Segment {
  label: string
  value: number
}

const props = defineProps<{
  segments: Segment[]
}>()

const COLORS = [
  '#4f7942',
  '#8b6914',
  '#c05746',
  '#3a7ca5',
  '#d4a017',
  '#7b5ea7',
  '#2e8b57',
  '#b5651d',
  '#6495ed',
  '#cd5c5c',
]

const total = computed(() => props.segments.reduce((sum, s) => sum + s.value, 0))

const conicGradient = computed(() => {
  if (total.value === 0) return 'background: #e5e7eb'
  let cumulative = 0
  const stops = props.segments.map((s, i) => {
    const start = cumulative
    cumulative += (s.value / total.value) * 360
    return `${COLORS[i % COLORS.length]} ${start}deg ${cumulative}deg`
  })
  return `background: conic-gradient(${stops.join(', ')})`
})
</script>

<template>
  <div v-if="total === 0" class="py-6 text-center text-brown">Aucune donnée</div>
  <div v-else class="flex flex-col items-center gap-4">
    <div class="size-40 rounded-full" :style="conicGradient"></div>
    <ul class="space-y-1 text-sm">
      <li v-for="(segment, i) in segments" :key="segment.label" class="flex items-center gap-2">
        <span
          class="inline-block size-3 rounded-full"
          :style="{ backgroundColor: COLORS[i % COLORS.length] }"
        ></span>
        <span class="text-dark">{{ segment.label }}</span>
        <span class="font-medium text-brown">({{ segment.value }})</span>
      </li>
    </ul>
  </div>
</template>
