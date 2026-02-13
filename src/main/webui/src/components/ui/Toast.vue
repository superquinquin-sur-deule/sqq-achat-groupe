<script setup lang="ts">
import { onMounted } from 'vue'

export interface ToastProps {
  variant?: 'success' | 'error' | 'info' | 'warning'
  message: string
  duration?: number
}

const props = withDefaults(defineProps<ToastProps>(), {
  variant: 'info',
  duration: 3000,
})

const emit = defineEmits<{
  close: []
}>()

onMounted(() => {
  if (props.duration > 0) {
    setTimeout(() => emit('close'), props.duration)
  }
})
</script>

<template>
  <div
    role="alert"
    aria-live="polite"
    :class="[
      'fixed left-1/2 top-4 z-50 flex min-h-[44px] -translate-x-1/2 items-center gap-2 rounded-lg px-4 py-3 shadow-lg',
      {
        'bg-green-100 text-green-800': variant === 'success',
        'bg-red-100 text-red-800': variant === 'error',
        'bg-blue-100 text-blue-800': variant === 'info',
        'bg-yellow-100 text-yellow-800': variant === 'warning',
      },
    ]"
  >
    <span class="text-sm font-medium">{{ message }}</span>
    <button
      type="button"
      class="ml-2 min-h-[44px] min-w-[44px] rounded p-1 hover:opacity-70 focus:outline-2 focus:outline-offset-2 focus:outline-dark"
      aria-label="Fermer la notification"
      @click="$emit('close')"
    >
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="h-4 w-4" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
      </svg>
    </button>
  </div>
</template>
