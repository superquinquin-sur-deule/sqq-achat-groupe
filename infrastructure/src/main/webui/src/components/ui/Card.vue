<script setup lang="ts">
import { cva } from 'class-variance-authority'

export interface CardProps {
  variant?: 'standard' | 'compact'
}

const props = withDefaults(defineProps<CardProps>(), {
  variant: 'standard',
})

const cardVariants = cva('rounded-xl bg-white shadow-sm', {
  variants: {
    variant: {
      standard: 'p-6',
      compact: 'p-4',
    },
  },
  defaultVariants: { variant: 'standard' },
})
</script>

<template>
  <div :class="cardVariants({ variant: props.variant })">
    <slot />
  </div>
</template>
