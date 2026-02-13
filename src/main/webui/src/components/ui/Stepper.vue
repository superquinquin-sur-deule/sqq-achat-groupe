<script setup lang="ts">
export interface StepperStep {
  label: string
}

export interface StepperProps {
  steps: StepperStep[]
  currentStep: number
}

defineProps<StepperProps>()
</script>

<template>
  <nav aria-label="Progression de la commande" class="flex items-center justify-center gap-2">
    <template v-for="(step, index) in steps" :key="index">
      <div class="flex items-center gap-2">
        <div
          :class="[
            'flex h-8 w-8 items-center justify-center rounded-full text-sm font-bold transition-colors',
            index + 1 === currentStep
              ? 'bg-dark text-white'
              : 'border border-gray-300 bg-transparent text-gray-400',
          ]"
          :aria-current="index + 1 === currentStep ? 'step' : undefined"
        >
          {{ index + 1 }}
        </div>
        <span
          :class="[
            'hidden text-sm font-medium md:inline',
            index + 1 === currentStep ? 'text-dark' : 'text-gray-400',
          ]"
        >
          {{ step.label }}
        </span>
      </div>
      <div
        v-if="index < steps.length - 1"
        class="h-px w-6 bg-gray-300"
        aria-hidden="true"
      />
    </template>
  </nav>
</template>
