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

const CX = 300
const CY = 150
const RADIUS = 80
const LABEL_RADIUS = 115

const slices = computed(() => {
  if (total.value === 0) return []
  let startAngle = -Math.PI / 2
  return props.segments.map((s, i) => {
    const angle = (s.value / total.value) * 2 * Math.PI
    const endAngle = startAngle + angle
    const midAngle = startAngle + angle / 2

    const x1 = CX + RADIUS * Math.cos(startAngle)
    const y1 = CY + RADIUS * Math.sin(startAngle)
    const x2 = CX + RADIUS * Math.cos(endAngle)
    const y2 = CY + RADIUS * Math.sin(endAngle)
    const largeArc = angle > Math.PI ? 1 : 0
    const path = `M ${CX} ${CY} L ${x1} ${y1} A ${RADIUS} ${RADIUS} 0 ${largeArc} 1 ${x2} ${y2} Z`

    const edgeX = CX + (RADIUS + 2) * Math.cos(midAngle)
    const edgeY = CY + (RADIUS + 2) * Math.sin(midAngle)
    const labelX = CX + LABEL_RADIUS * Math.cos(midAngle)
    const labelY = CY + LABEL_RADIUS * Math.sin(midAngle)
    const isLeftSide = Math.cos(midAngle) < 0

    const result = {
      path,
      color: COLORS[i % COLORS.length],
      label: s.label,
      value: s.value,
      edgeX,
      edgeY,
      labelX,
      labelY,
      textAnchor: isLeftSide ? 'end' : 'start',
      textX: labelX + (isLeftSide ? -4 : 4),
    }
    startAngle = endAngle
    return result
  })
})
</script>

<template>
  <div v-if="total === 0" class="py-6 text-center text-brown">Aucune donnée</div>
  <svg v-else viewBox="0 0 600 300" class="mx-auto w-full">
    <template v-if="slices.length === 1">
      <circle :cx="CX" :cy="CY" :r="RADIUS" :fill="slices[0]!.color" />
    </template>
    <template v-else>
      <path v-for="(slice, i) in slices" :key="'arc-' + i" :d="slice.path" :fill="slice.color" />
    </template>
    <g v-for="(slice, i) in slices" :key="'label-' + i">
      <line
        :x1="slice.edgeX"
        :y1="slice.edgeY"
        :x2="slice.labelX"
        :y2="slice.labelY"
        stroke="#9ca3af"
        stroke-width="0.8"
      />
      <text
        :x="slice.textX"
        :y="slice.labelY"
        :text-anchor="slice.textAnchor"
        dominant-baseline="middle"
        font-size="11"
        fill="#4a3728"
      >
        {{ slice.label }} ({{ slice.value }})
      </text>
    </g>
  </svg>
</template>
