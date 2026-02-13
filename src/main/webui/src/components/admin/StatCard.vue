<script setup lang="ts">
import Card from '@/components/ui/Card.vue'

const props = defineProps<{
  label: string
  value: string | number
  unit?: string
}>()

function slugify(text: string): string {
  return text
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/(^-|-$)/g, '')
}
</script>

<template>
  <Card :data-testid="'stat-' + slugify(props.label)">
    <div class="flex flex-col items-center p-2">
      <span class="text-[32px] font-bold leading-tight text-dark">
        {{ value }}<span v-if="unit" class="ml-1 text-lg font-normal text-brown">{{ unit }}</span>
      </span>
      <span class="mt-1 text-sm text-brown">{{ label }}</span>
    </div>
  </Card>
</template>
