<script setup lang="ts">
defineProps<{
  columns: string[]
  caption: string
  dataTestid?: string
}>()
</script>

<template>
  <div class="overflow-x-auto rounded-xl border border-gray-200 bg-white">
    <table class="w-full" :data-testid="dataTestid">
      <caption class="sr-only">{{ caption }}</caption>
      <thead>
        <tr class="bg-dark text-left text-sm text-white">
          <th v-for="col in columns" :key="col" class="px-4 py-3 font-medium">
            {{ col }}
          </th>
        </tr>
      </thead>
      <tbody>
        <slot />
      </tbody>
    </table>
  </div>
</template>
