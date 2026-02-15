<script lang="ts">
import { cva, type VariantProps } from 'class-variance-authority'

export const buttonVariants = cva(
  'inline-flex items-center justify-center rounded-lg font-semibold transition-colors focus:outline-2 focus:outline-offset-2 focus:outline-dark',
  {
    variants: {
      variant: {
        primary: 'bg-primary text-dark hover:bg-primary/90 active:bg-primary/80',
        secondary: 'border-2 border-dark bg-white text-dark hover:bg-surface active:bg-surface/80',
        danger: 'bg-red-600 text-white hover:bg-red-700 active:bg-red-800',
        ghost: 'bg-transparent text-dark hover:bg-surface active:bg-surface/80',
      },
      size: {
        md: 'min-h-[44px] px-4 py-2',
        lg: 'min-h-[44px] w-full px-6 py-3',
        icon: 'min-h-[44px] min-w-[44px] px-0 py-2',
      },
      disabled: {
        true: 'cursor-not-allowed opacity-50',
      },
    },
    defaultVariants: { variant: 'primary', size: 'md' },
  },
)

export type ButtonVariants = VariantProps<typeof buttonVariants>
</script>

<script setup lang="ts">
export interface ButtonProps {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost'
  size?: 'md' | 'lg' | 'icon'
  disabled?: boolean
  loading?: boolean
  type?: 'button' | 'submit' | 'reset'
}

const props = withDefaults(defineProps<ButtonProps>(), {
  variant: 'primary',
  size: 'md',
  disabled: false,
  loading: false,
  type: 'button',
})
</script>

<template>
  <button
    :type="type"
    :disabled="disabled || loading"
    :class="
      buttonVariants({
        variant: props.variant,
        size: props.size,
        disabled: disabled || loading || undefined,
      })
    "
  >
    <svg
      v-if="loading"
      class="mr-2 h-4 w-4 animate-spin"
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
      viewBox="0 0 24 24"
      aria-hidden="true"
    >
      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
      <path
        class="opacity-75"
        fill="currentColor"
        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
      />
    </svg>
    <slot />
  </button>
</template>
