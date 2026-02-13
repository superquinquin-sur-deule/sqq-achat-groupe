<script setup lang="ts">
export interface CardProps {
  variant?: 'standard' | 'compact'
}

withDefaults(defineProps<CardProps>(), {
  variant: 'standard',
})
</script>

<template>
  <div
    :class="[
      'rounded-xl bg-white shadow-sm',
      {
        'p-6': variant === 'standard',
        'p-4': variant === 'compact',
      },
    ]"
  >
    <slot />
  </div>
</template>
