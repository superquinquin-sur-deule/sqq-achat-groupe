<script setup lang="ts">
defineProps<{
  hasNext: boolean
  hasPrevious: boolean
  pageNumber: number
  loading?: boolean
}>()

defineEmits<{
  next: []
  previous: []
}>()
</script>

<template>
  <div class="mt-4 flex items-center justify-between" data-testid="pagination">
    <button
      :disabled="!hasPrevious || loading"
      class="inline-flex min-h-[44px] items-center rounded-lg border-2 border-dark bg-white px-4 py-2 font-semibold text-dark transition-colors hover:bg-surface active:bg-surface/80 disabled:cursor-not-allowed disabled:opacity-50"
      data-testid="pagination-prev"
      @click="$emit('previous')"
    >
      Précédent
    </button>
    <span class="text-sm text-brown" data-testid="pagination-page">Page {{ pageNumber }}</span>
    <button
      :disabled="!hasNext || loading"
      class="inline-flex min-h-[44px] items-center rounded-lg border-2 border-dark bg-white px-4 py-2 font-semibold text-dark transition-colors hover:bg-surface active:bg-surface/80 disabled:cursor-not-allowed disabled:opacity-50"
      data-testid="pagination-next"
      @click="$emit('next')"
    >
      Suivant
    </button>
  </div>
</template>
