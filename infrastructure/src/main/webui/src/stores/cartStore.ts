import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import type { CartItem } from '@/types/cart'
import type { ProductResponse, ProductVariantResponse } from '@/api/generated/model'

export const useCartStore = defineStore(
  'cart',
  () => {
    const items = ref<CartItem[]>([])
    const venteId = ref<number | null>(null)

    const total = computed(() =>
      items.value.reduce((sum, item) => sum + item.price * item.quantity, 0),
    )

    const itemCount = computed(() => items.value.reduce((count, item) => count + item.quantity, 0))

    const isEmpty = computed(() => items.value.length === 0)

    function setVenteId(id: number) {
      if (venteId.value !== null && venteId.value !== id) {
        clearCart()
      }
      venteId.value = id
    }

    function addItem(product: ProductResponse, variant: ProductVariantResponse) {
      const existing = items.value.find((item) => item.variantId === variant.id)
      if (existing) {
        if (existing.quantity < existing.stock) {
          existing.quantity++
        }
      } else {
        items.value.push({
          productId: product.id,
          variantId: variant.id,
          variantLabel: variant.label,
          name: product.name,
          price: variant.prixTtc,
          supplier: product.supplier,
          quantity: 1,
          stock: variant.stock,
        })
      }
    }

    function updateQuantity(variantId: number, quantity: number) {
      const item = items.value.find((i) => i.variantId === variantId)
      if (item && quantity >= 1) {
        item.quantity = Math.min(quantity, item.stock)
      }
    }

    function isMaxQuantity(variantId: number): boolean {
      const item = items.value.find((i) => i.variantId === variantId)
      return item ? item.quantity >= item.stock : false
    }

    function removeItem(variantId: number) {
      items.value = items.value.filter((item) => item.variantId !== variantId)
    }

    function clearCart() {
      items.value = []
    }

    function getItemQuantity(variantId: number): number {
      return items.value.find((i) => i.variantId === variantId)?.quantity ?? 0
    }

    /** Libellé affiché en panier : « Sapin Nordmann — 200cm » pour un produit à variantes. */
    function itemLabel(item: CartItem): string {
      return item.variantLabel ? `${item.name} — ${item.variantLabel}` : item.name
    }

    return {
      items,
      venteId,
      total,
      itemCount,
      isEmpty,
      setVenteId,
      addItem,
      updateQuantity,
      removeItem,
      clearCart,
      getItemQuantity,
      isMaxQuantity,
      itemLabel,
    }
  },
  {
    // Nouvelle clé : les paniers déjà persistés n'ont pas de variantId et casseraient le checkout.
    persist: { key: 'cart-v2' },
  },
)
