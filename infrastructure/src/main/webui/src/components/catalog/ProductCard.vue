<script setup lang="ts">
import { computed } from 'vue'
import { cva } from 'class-variance-authority'
import type { ProductResponse } from '@/api/generated/model'
import Card from '@/components/ui/Card.vue'
import Button from '@/components/ui/Button.vue'
import { useCartStore } from '@/stores/cartStore'

const props = defineProps<{
  product: ProductResponse
}>()

const emit = defineEmits<{
  add: [product: ProductResponse]
}>()

const cartStore = useCartStore()

const isExhausted = computed(() => props.product.stock === 0)

const quantity = computed(() => cartStore.getItemQuantity(props.product.id))

const isInCart = computed(() => quantity.value > 0)

const formattedPrice = computed(() =>
  new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(
    props.product.price,
  ),
)

const productCardVariants = cva('transition-shadow hover:shadow-md', {
  variants: {
    exhausted: {
      true: 'opacity-50',
    },
  },
})

function handleAdd() {
  if (!isExhausted.value) {
    emit('add', props.product)
  }
}

function increase() {
  cartStore.updateQuantity(props.product.id, quantity.value + 1)
}

function decrease() {
  if (quantity.value <= 1) {
    cartStore.removeItem(props.product.id)
  } else {
    cartStore.updateQuantity(props.product.id, quantity.value - 1)
  }
}

function onQuantityInput(event: Event) {
  const value = parseInt((event.target as HTMLInputElement).value)
  if (!value || value < 1) {
    cartStore.removeItem(props.product.id)
  } else {
    cartStore.updateQuantity(props.product.id, value)
  }
}
</script>

<template>
  <div
    data-testid="product-card"
    :data-exhausted="isExhausted || undefined"
    :class="productCardVariants({ exhausted: isExhausted || undefined })"
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

      <div v-if="isInCart" class="mt-4 flex w-full items-center justify-center gap-2">
        <Button
          data-testid="decrease-quantity"
          variant="secondary"
          size="icon"
          :aria-label="`Diminuer la quantité de ${product.name}`"
          @click="decrease"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 20 20"
            fill="currentColor"
            class="h-5 w-5"
            aria-hidden="true"
          >
            <path
              fill-rule="evenodd"
              d="M4 10a.75.75 0 0 1 .75-.75h10.5a.75.75 0 0 1 0 1.5H4.75A.75.75 0 0 1 4 10Z"
              clip-rule="evenodd"
            />
          </svg>
        </Button>

        <input
          type="number"
          data-testid="item-quantity"
          :value="quantity"
          min="1"
          class="min-h-[44px] w-14 appearance-none rounded-lg border-2 border-dark bg-white text-center text-base font-semibold text-dark [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none [-moz-appearance:textfield] focus:outline-2 focus:outline-offset-2 focus:outline-dark"
          :aria-label="`Quantité de ${product.name}`"
          @change="onQuantityInput"
          @blur="onQuantityInput"
        />

        <Button
          data-testid="increase-quantity"
          variant="secondary"
          size="icon"
          :aria-label="`Augmenter la quantité de ${product.name}`"
          @click="increase"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 20 20"
            fill="currentColor"
            class="h-5 w-5"
            aria-hidden="true"
          >
            <path
              d="M10.75 4.75a.75.75 0 0 0-1.5 0v4.5h-4.5a.75.75 0 0 0 0 1.5h4.5v4.5a.75.75 0 0 0 1.5 0v-4.5h4.5a.75.75 0 0 0 0-1.5h-4.5v-4.5Z"
            />
          </svg>
        </Button>
      </div>

      <Button
        v-else
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
