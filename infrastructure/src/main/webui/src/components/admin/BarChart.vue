<script setup lang="ts">
import { computed } from 'vue'

interface Segment {
  label: string
  value: number
}

const props = defineProps<{
  segments: Segment[]
}>()

const maxValue = computed(() => Math.max(...props.segments.map((s) => s.value), 1))
</script>

<template>
  <div v-if="segments.length === 0" class="py-6 text-center text-brown">Aucune donnée</div>
  <div v-else class="flex items-end gap-2 overflow-x-auto pb-2">
    <div
      v-for="segment in segments"
      :key="segment.label"
      class="flex min-w-10 flex-1 flex-col items-center gap-1"
    >
      <span class="text-xs font-medium text-brown">{{ segment.value }}</span>
      <div
        class="w-full rounded-t bg-primary/70"
        :style="{ height: `${Math.max((segment.value / maxValue) * 160, 4)}px` }"
      ></div>
      <span class="max-w-full truncate text-center text-xs text-dark" :title="segment.label">{{
        segment.label
      }}</span>
    </div>
  </div>
</template>
