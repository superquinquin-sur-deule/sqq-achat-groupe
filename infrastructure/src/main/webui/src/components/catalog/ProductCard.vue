<script setup lang="ts">
import { computed } from 'vue'
import { cva } from 'class-variance-authority'
import type { ProductResponse } from '@/api/generated/model'
import type { RayonColor } from '@/utils/rayon-colors'
import Card from '@/components/ui/Card.vue'
import Button from '@/components/ui/Button.vue'
import { useCartStore } from '@/stores/cartStore'

const props = defineProps<{
  product: ProductResponse
  rayonColor: RayonColor
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
      <div
        class="-mx-6 -mt-6 mb-4 flex aspect-[4/3] items-center justify-center overflow-hidden rounded-t-xl"
        :class="rayonColor.bg"
      >
        <img
          v-if="product.imageUrl"
          :src="product.imageUrl"
          :alt="product.name"
          class="h-full w-full object-cover"
        />
        <svg
          v-else
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1"
          stroke="currentColor"
          class="h-12 w-12"
          :class="rayonColor.text"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="m2.25 15.75 5.159-5.159a2.25 2.25 0 0 1 3.182 0l5.159 5.159m-1.5-1.5 1.409-1.409a2.25 2.25 0 0 1 3.182 0l2.909 2.909M3.75 21h16.5A2.25 2.25 0 0 0 22.5 19.5V4.5a2.25 2.25 0 0 0-2.25-2.25H3.75A2.25 2.25 0 0 0 1.5 4.5v15a2.25 2.25 0 0 0 2.25 2.25Z"
          />
        </svg>
      </div>
      <h3 class="text-xl font-semibold text-dark">{{ product.name }}</h3>
      <div class="mt-1 flex flex-wrap gap-2">
        <span
          data-testid="product-brand"
          class="inline-block rounded-full bg-brown/10 px-2 py-0.5 text-xs font-medium text-brown"
          >{{ product.brand }}</span
        >
      </div>
      <p data-testid="product-description" class="mt-2 text-base text-dark">
        {{ product.description }}
      </p>
      <div class="mt-4 flex items-center justify-between">
        <span
          data-testid="product-price"
          class="rounded-full bg-primary px-3 py-1 text-sm font-semibold text-dark"
        >
          {{ formattedPrice }}
        </span>
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
        class="mt-4 w-full transition-transform hover:scale-[1.02] active:scale-[0.98]"
        :disabled="isExhausted"
        :aria-label="`Ajouter ${product.name} au panier`"
        @click="handleAdd"
      >
        Ajouter
      </Button>
    </Card>
  </div>
</template>
