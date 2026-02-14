<script setup lang="ts">
import { computed } from 'vue'
import type { ProductResponse } from '@/api/generated/model'
import Card from '@/components/ui/Card.vue'
import Button from '@/components/ui/Button.vue'

const props = defineProps<{
  product: ProductResponse
}>()

const emit = defineEmits<{
  add: [product: ProductResponse]
}>()

const isExhausted = computed(() => props.product.stock === 0)

const formattedPrice = computed(() =>
  new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(
    props.product.price,
  ),
)

function handleAdd() {
  if (!isExhausted.value) {
    emit('add', props.product)
  }
}
</script>

<template>
  <div
    data-testid="product-card"
    :data-exhausted="isExhausted || undefined"
    :class="['transition-shadow hover:shadow-md', { 'opacity-50': isExhausted }]"
  >
    <Card>
      <h3 class="text-xl font-semibold text-dark">{{ product.name }}</h3>
      <p data-testid="product-supplier" class="mt-1 text-sm text-brown">{{ product.supplier }}</p>
      <p data-testid="product-description" class="mt-2 text-base text-dark">
        {{ product.description }}
      </p>
      <div class="mt-4 flex items-center justify-between">
        <p data-testid="product-price" class="text-lg font-medium text-dark">
          {{ formattedPrice }}
        </p>
        <span
          v-if="isExhausted"
          data-testid="exhausted-badge"
          class="text-sm font-medium text-red-600"
        >
          Épuisé
        </span>
      </div>
      <Button
        data-testid="add-button"
        variant="primary"
        class="mt-4 w-full"
        :disabled="isExhausted"
        :aria-label="`Ajouter ${product.name} au panier`"
        @click="handleAdd"
      >
        Ajouter
      </Button>
    </Card>
  </div>
</template>
