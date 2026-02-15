<script setup lang="ts">
import { cva } from 'class-variance-authority'

export interface StepperStep {
  label: string
}

export interface StepperProps {
  steps: StepperStep[]
  currentStep: number
}

defineProps<StepperProps>()

const circleVariants = cva(
  'flex h-8 w-8 items-center justify-center rounded-full text-sm font-bold transition-colors',
  {
    variants: {
      active: {
        true: 'bg-dark text-white',
        false: 'border border-gray-300 bg-transparent text-gray-400',
      },
    },
  },
)

const labelVariants = cva('hidden text-sm font-medium md:inline', {
  variants: {
    active: {
      true: 'text-dark',
      false: 'text-gray-400',
    },
  },
})
</script>

<template>
  <nav aria-label="Progression de la commande" class="flex items-center justify-center gap-2">
    <template v-for="(step, index) in steps" :key="index">
      <div class="flex items-center gap-2">
        <div
          :class="circleVariants({ active: index + 1 === currentStep })"
          :aria-current="index + 1 === currentStep ? 'step' : undefined"
        >
          {{ index + 1 }}
        </div>
        <span :class="labelVariants({ active: index + 1 === currentStep })">
          {{ step.label }}
        </span>
      </div>
      <div v-if="index < steps.length - 1" class="h-px w-6 bg-gray-300" aria-hidden="true" />
    </template>
  </nav>
</template>
