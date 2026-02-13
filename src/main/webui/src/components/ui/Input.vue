<script setup lang="ts">
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

withDefaults(defineProps<InputProps>(), {
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
      :class="[
        'min-h-[44px] rounded-lg border px-3 py-2 text-dark transition-colors focus:outline-2 focus:outline-offset-2 focus:outline-dark',
        {
          'border-gray-300 focus:border-primary': !error && !disabled,
          'border-red-600 focus:border-red-600': error,
          'cursor-not-allowed border-gray-200 bg-gray-100 opacity-50': disabled,
        },
      ]"
      @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
      @blur="$emit('blur', $event)"
    />
    <p v-if="error" :id="`${id}-error`" class="text-sm text-red-600" role="alert">
      {{ error }}
    </p>
  </div>
</template>
