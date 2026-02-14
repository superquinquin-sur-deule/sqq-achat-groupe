<script setup lang="ts">
import { computed } from 'vue'
import type { CartItem } from '@/types/cart'
import { useCartStore } from '@/stores/cartStore'
import Button from '@/components/ui/Button.vue'

const props = defineProps<{
  item: CartItem
}>()

const cartStore = useCartStore()

const subtotal = computed(() =>
  new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(
    props.item.price * props.item.quantity,
  ),
)

const formattedPrice = computed(() =>
  new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(props.item.price),
)

const isMinQuantity = computed(() => props.item.quantity <= 1)

function increase() {
  cartStore.updateQuantity(props.item.productId, props.item.quantity + 1)
}

function decrease() {
  if (!isMinQuantity.value) {
    cartStore.updateQuantity(props.item.productId, props.item.quantity - 1)
  }
}

function remove() {
  cartStore.removeItem(props.item.productId)
}
</script>

<template>
  <div
    data-testid="cart-item"
    class="flex flex-col gap-2 border-b border-gray-200 py-4 sm:flex-row sm:items-center sm:gap-4"
  >
    <div class="flex-1 min-w-0">
      <p data-testid="item-name" class="text-base font-medium text-dark truncate">
        {{ item.name }}
      </p>
      <p data-testid="item-price" class="mt-1 text-sm text-brown">{{ formattedPrice }}</p>
    </div>

    <div class="flex items-center gap-2">
      <Button
        data-testid="decrease-quantity"
        variant="secondary"
        size="icon"
        :disabled="isMinQuantity"
        :aria-label="`Diminuer la quantité de ${item.name}`"
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

      <span
        data-testid="item-quantity"
        class="inline-flex w-12 items-center justify-center text-base font-medium text-dark"
        aria-label="Quantité"
      >
        {{ item.quantity }}
      </span>

      <Button
        data-testid="increase-quantity"
        variant="secondary"
        size="icon"
        :aria-label="`Augmenter la quantité de ${item.name}`"
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

    <p data-testid="item-subtotal" class="w-24 text-right text-lg font-medium text-dark sm:ml-auto">
      {{ subtotal }}
    </p>

    <Button
      data-testid="remove-item"
      variant="ghost"
      size="icon"
      :aria-label="`Supprimer ${item.name} du panier`"
      class="text-red-600 hover:text-red-700"
      @click="remove"
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
          d="M8.75 1A2.75 2.75 0 0 0 6 3.75v.443c-.795.077-1.584.176-2.365.298a.75.75 0 1 0 .23 1.482l.149-.022.841 10.518A2.75 2.75 0 0 0 7.596 19h4.807a2.75 2.75 0 0 0 2.742-2.53l.841-10.52.149.023a.75.75 0 0 0 .23-1.482A41.03 41.03 0 0 0 14 4.193V3.75A2.75 2.75 0 0 0 11.25 1h-2.5ZM10 4c.84 0 1.673.025 2.5.075V3.75c0-.69-.56-1.25-1.25-1.25h-2.5c-.69 0-1.25.56-1.25 1.25v.325C8.327 4.025 9.16 4 10 4ZM8.58 7.72a.75.75 0 0 1 .7.798l-.5 5.5a.75.75 0 0 1-1.498-.136l.5-5.5a.75.75 0 0 1 .798-.662Zm2.84 0a.75.75 0 0 1 .798.662l.5 5.5a.75.75 0 1 1-1.498.136l-.5-5.5a.75.75 0 0 1 .7-.798Z"
          clip-rule="evenodd"
        />
      </svg>
    </Button>
  </div>
</template>
