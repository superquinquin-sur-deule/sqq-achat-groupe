<script setup lang="ts">
import type { ProductResponse } from '@/api/generated/model'
import ProductCard from './ProductCard.vue'

defineProps<{
  products: ProductResponse[]
  loading: boolean
}>()

const emit = defineEmits<{
  add: [product: ProductResponse]
}>()
</script>

<template>
  <div class="mx-auto max-w-5xl px-4 md:px-6 lg:px-8">
    <!-- Skeleton screens -->
    <div
      v-if="loading"
      data-testid="product-grid"
      class="grid grid-cols-1 gap-4 md:grid-cols-2 md:gap-6 lg:grid-cols-3"
    >
      <div
        v-for="n in 6"
        :key="n"
        data-testid="skeleton-card"
        class="h-[200px] animate-pulse rounded-xl bg-gray-200"
      />
    </div>

    <!-- Product cards -->
    <div
      v-else
      data-testid="product-grid"
      class="grid grid-cols-1 gap-4 md:grid-cols-2 md:gap-6 lg:grid-cols-3"
    >
      <ProductCard
        v-for="product in products"
        :key="product.id"
        :product="product"
        @add="emit('add', $event)"
      />
    </div>
  </div>
</template>
