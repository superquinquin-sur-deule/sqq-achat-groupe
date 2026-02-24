<script setup lang="ts">
import { computed } from 'vue'
import type { ProductResponse } from '@/api/generated/model'
import { getRayonColor, toRayonId } from '@/utils/rayon-colors'
import ProductCard from './ProductCard.vue'

const props = defineProps<{
  products: ProductResponse[]
  loading: boolean
}>()

const emit = defineEmits<{
  add: [product: ProductResponse]
}>()

const groupedProducts = computed(() => {
  const groups = new Map<string, ProductResponse[]>()
  for (const product of props.products) {
    const cat = product.category || 'Autres'
    if (!groups.has(cat)) groups.set(cat, [])
    groups.get(cat)!.push(product)
  }
  return Array.from(groups.entries()).map(([category, products], index) => ({
    category,
    products,
    color: getRayonColor(index),
  }))
})
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

    <!-- Empty state -->
    <p v-else-if="products.length === 0" class="py-12 text-center text-brown">
      Aucun produit disponible pour le moment.
    </p>

    <!-- Product cards grouped by rayon -->
    <div v-else data-testid="product-grid">
      <div
        v-for="group in groupedProducts"
        :key="group.category"
        :id="toRayonId(group.category)"
        class="mb-8 scroll-mt-16"
      >
        <h2 class="mb-4 border-l-4 pl-3 text-xl font-bold text-dark" :class="group.color.border">
          {{ group.category }}
        </h2>
        <div class="grid grid-cols-1 gap-4 md:grid-cols-2 md:gap-6 lg:grid-cols-3">
          <ProductCard
            v-for="product in group.products"
            :key="product.id"
            :product="product"
            :rayon-color="group.color"
            @add="emit('add', $event)"
          />
        </div>
      </div>
    </div>
  </div>
</template>
