<script setup lang="ts">
import { cva } from 'class-variance-authority'

export interface InputProps {
  modelValue?: string
  label: string
  id: string
  type?: string
  placeholder?: string
  error?: string
  disabled?: boolean
  required?: boolean
  autocomplete?: string
}

defineOptions({ inheritAttrs: false })

const props = withDefaults(defineProps<InputProps>(), {
  modelValue: '',
  type: 'text',
  placeholder: '',
  error: '',
  disabled: false,
  required: false,
  autocomplete: undefined,
})

defineEmits<{
  'update:modelValue': [value: string]
  blur: [event: FocusEvent]
}>()

const inputVariants = cva(
  'min-h-[44px] rounded-lg border px-3 py-2 text-dark transition-colors focus:outline-2 focus:outline-offset-2 focus:outline-dark',
  {
    variants: {
      error: {
        true: 'border-red-600 focus:border-red-600',
        false: 'border-gray-300 focus:border-primary',
      },
      disabled: {
        true: 'cursor-not-allowed border-gray-200 bg-gray-100 opacity-50',
      },
    },
    defaultVariants: { error: false },
  },
)
</script>

<template>
  <div class="flex flex-col gap-1">
    <label :for="id" class="text-sm font-medium text-dark">
      {{ label }}
      <span v-if="required" class="text-red-600" aria-hidden="true">*</span>
    </label>
    <input
      :id="id"
      :type="type"
      :value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :required="required"
      :autocomplete="autocomplete"
      :aria-describedby="error ? `${id}-error` : undefined"
      :aria-invalid="!!error"
      v-bind="$attrs"
      :class="inputVariants({ error: !!props.error, disabled: disabled || undefined })"
      @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
      @blur="$emit('blur', $event)"
    />
    <p v-if="error" :id="`${id}-error`" class="text-sm text-red-600" role="alert">
      {{ error }}
    </p>
  </div>
</template>
