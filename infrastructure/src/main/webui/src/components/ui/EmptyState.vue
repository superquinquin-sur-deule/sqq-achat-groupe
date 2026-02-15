<script setup lang="ts">
import Button from './Button.vue'

export interface EmptyStateProps {
  message: string
  actionLabel?: string
  dataTestid?: string
}

defineProps<EmptyStateProps>()
const emit = defineEmits<{ action: [] }>()
</script>

<template>
  <div
    class="rounded-xl border border-gray-200 bg-white p-12 text-center"
    :data-testid="dataTestid ?? 'empty-state'"
  >
    <p :class="['text-brown', { 'mb-4': actionLabel }]">{{ message }}</p>
    <Button v-if="actionLabel" variant="primary" @click="emit('action')">
      {{ actionLabel }}
    </Button>
  </div>
</template>
