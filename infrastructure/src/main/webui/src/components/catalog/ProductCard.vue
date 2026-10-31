<script setup lang="ts">
import { computed, ref } from 'vue'
import { cva } from 'class-variance-authority'
import type { ProductResponse, ProductVariantResponse } from '@/api/generated/model'
import type { RayonColor } from '@/utils/rayon-colors'
import Card from '@/components/ui/Card.vue'
import Button from '@/components/ui/Button.vue'
import { useCartStore } from '@/stores/cartStore'

const props = defineProps<{
  product: ProductResponse
  rayonColor: RayonColor
}>()

const emit = defineEmits<{
  add: [product: ProductResponse, variant: ProductVariantResponse]
}>()

const cartStore = useCartStore()

const variants = computed(() => props.product.variants ?? [])

/**
 * Un produit « simple » n'a qu'une variante, au libellé vide : on garde alors
 * exactement l'ancienne carte, sans sélecteur.
 */
const hasVariantChoice = computed(() => variants.value.length > 1)

const soleVariant = computed(() => (hasVariantChoice.value ? null : (variants.value[0] ?? null)))

/** Rien n'est présélectionné : le client choisit sa variante avant de pouvoir ajouter. */
const selectedVariantId = ref<number | null>(null)

const selectedVariant = computed<ProductVariantResponse | null>(() => {
  if (soleVariant.value) return soleVariant.value
  return variants.value.find((v) => v.id === selectedVariantId.value) ?? null
})

const isExhausted = computed(() => variants.value.every((v) => v.stock === 0))

const quantity = computed(() =>
  selectedVariant.value ? cartStore.getItemQuantity(selectedVariant.value.id) : 0,
)

const isInCart = computed(() => quantity.value > 0)

const isMaxQuantity = computed(
  () => !!selectedVariant.value && quantity.value >= selectedVariant.value.stock,
)

const canAdd = computed(
  () => !!selectedVariant.value && selectedVariant.value.stock > 0 && !isMaxQuantity.value,
)

function formatPrice(amount: number): string {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(amount)
}

/** Prix affiché en tête de carte : celui de la variante choisie, sinon le plus bas. */
const formattedPrice = computed(() => {
  if (selectedVariant.value) return formatPrice(selectedVariant.value.prixTtc)
  if (variants.value.length === 0) return ''
  const min = Math.min(...variants.value.map((v) => v.prixTtc))
  return formatPrice(min)
})

const productCardVariants = cva('', {
  variants: {
    exhausted: {
      true: 'opacity-50',
    },
  },
})

const variantOptionVariants = cva(
  'flex min-h-[44px] w-full items-center justify-between gap-3 rounded-lg border px-3 py-2 text-left text-sm font-medium transition-colors focus:outline-2 focus:outline-offset-2 focus:outline-dark',
  {
    variants: {
      state: {
        selected: 'border-primary bg-primary text-dark',
        available: 'border-gray-300 bg-white text-dark hover:border-primary',
        disabled: 'cursor-not-allowed border-gray-200 bg-gray-100 text-gray-400',
      },
    },
    defaultVariants: { state: 'available' },
  },
)

function variantState(variant: ProductVariantResponse): 'selected' | 'available' | 'disabled' {
  if (variant.stock === 0) return 'disabled'
  if (selectedVariantId.value === variant.id) return 'selected'
  return 'available'
}

function selectVariant(variant: ProductVariantResponse) {
  if (variant.stock > 0) {
    selectedVariantId.value = variant.id
  }
}

function handleAdd() {
  if (canAdd.value && selectedVariant.value) {
    emit('add', props.product, selectedVariant.value)
  }
}

function increase() {
  if (!isMaxQuantity.value && selectedVariant.value) {
    cartStore.updateQuantity(selectedVariant.value.id, quantity.value + 1)
  }
}

function decrease() {
  if (!selectedVariant.value) return
  if (quantity.value <= 1) {
    cartStore.removeItem(selectedVariant.value.id)
  } else {
    cartStore.updateQuantity(selectedVariant.value.id, quantity.value - 1)
  }
}

function onQuantityInput(event: Event) {
  if (!selectedVariant.value) return
  const value = parseInt((event.target as HTMLInputElement).value)
  if (!value || value < 1) {
    cartStore.removeItem(selectedVariant.value.id)
  } else {
    cartStore.updateQuantity(selectedVariant.value.id, Math.min(value, selectedVariant.value.stock))
  }
}
</script>

<template>
  <div
    data-testid="product-card"
    class="h-full"
    :data-exhausted="isExhausted || undefined"
    :class="productCardVariants({ exhausted: isExhausted || undefined })"
  >
    <Card class="h-full flex flex-col transition-shadow hover:shadow-md">
      <div
        class="-mx-6 -mt-6 mb-4 flex aspect-[4/3] items-center justify-center overflow-hidden rounded-t-xl bg-white"
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
      <p
        v-if="product.importantNote"
        data-testid="product-important-note"
        class="mt-3 flex gap-2 rounded-lg border border-amber-300 bg-amber-50 px-3 py-2 text-sm font-medium text-amber-900"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 20 20"
          fill="currentColor"
          class="mt-0.5 h-4 w-4 shrink-0"
          aria-hidden="true"
        >
          <path
            fill-rule="evenodd"
            d="M8.485 2.495c.673-1.167 2.357-1.167 3.03 0l6.28 10.875c.673 1.167-.17 2.625-1.516 2.625H3.72c-1.347 0-2.189-1.458-1.515-2.625L8.485 2.495ZM10 5a.75.75 0 0 1 .75.75v3.5a.75.75 0 0 1-1.5 0v-3.5A.75.75 0 0 1 10 5Zm0 9a1 1 0 1 0 0-2 1 1 0 0 0 0 2Z"
            clip-rule="evenodd"
          />
        </svg>
        <span><span class="sr-only">Mention importante : </span>{{ product.importantNote }}</span>
      </p>

      <div
        v-if="hasVariantChoice"
        data-testid="variant-selector"
        class="mt-4 flex flex-col gap-2"
        role="radiogroup"
        :aria-label="`Choisir une déclinaison de ${product.name}`"
      >
        <button
          v-for="variant in variants"
          :key="variant.id"
          type="button"
          role="radio"
          data-testid="variant-option"
          :data-variant-id="variant.id"
          :data-selected="selectedVariantId === variant.id || undefined"
          :data-exhausted="variant.stock === 0 || undefined"
          :aria-checked="selectedVariantId === variant.id"
          :disabled="variant.stock === 0"
          :class="variantOptionVariants({ state: variantState(variant) })"
          @click="selectVariant(variant)"
        >
          <span data-testid="variant-option-label">{{ variant.label }}</span>
          <span class="flex items-center gap-2">
            <span v-if="variant.stock === 0" class="text-xs font-medium text-red-600">Épuisé</span>
            <span data-testid="variant-option-price" class="font-semibold">{{
              formatPrice(variant.prixTtc)
            }}</span>
          </span>
        </button>
      </div>

      <div class="mt-auto">
        <div class="mt-4 flex items-center justify-between">
          <span
            data-testid="product-price"
            class="rounded-full bg-primary px-3 py-1 text-sm font-semibold text-dark"
          >
            <span v-if="hasVariantChoice && !selectedVariant">À partir de </span
            >{{ formattedPrice }}
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
            :max="selectedVariant?.stock"
            class="min-h-[44px] w-14 appearance-none rounded-lg border-2 border-dark bg-white text-center text-base font-semibold text-dark [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none [-moz-appearance:textfield] focus:outline-2 focus:outline-offset-2 focus:outline-dark"
            :aria-label="`Quantité de ${product.name}`"
            @change="onQuantityInput"
            @blur="onQuantityInput"
          />

          <Button
            data-testid="increase-quantity"
            variant="secondary"
            size="icon"
            :disabled="isMaxQuantity"
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
          :disabled="!canAdd"
          :aria-label="`Ajouter ${product.name} au panier`"
          @click="handleAdd"
        >
          Ajouter
        </Button>
      </div>
    </Card>
  </div>
</template>
