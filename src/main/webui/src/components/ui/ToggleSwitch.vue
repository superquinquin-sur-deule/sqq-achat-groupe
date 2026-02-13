<script setup lang="ts">
defineProps<{
  modelValue: boolean
  label: string
  disabled?: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

function toggle(current: boolean) {
  emit('update:modelValue', !current)
}
</script>

<template>
  <button
    type="button"
    role="switch"
    :aria-checked="modelValue"
    :aria-label="label"
    :disabled="disabled"
    :class="[
      'relative inline-flex h-7 w-12 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2',
      modelValue ? 'bg-primary' : 'bg-gray-300',
      disabled ? 'cursor-not-allowed opacity-50' : '',
    ]"
    @click="toggle(modelValue)"
    @keydown.space.prevent="toggle(modelValue)"
    @keydown.enter.prevent="toggle(modelValue)"
  >
    <span
      :class="[
        'pointer-events-none inline-block h-6 w-6 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
        modelValue ? 'translate-x-5' : 'translate-x-0',
      ]"
    />
  </button>
</template>
