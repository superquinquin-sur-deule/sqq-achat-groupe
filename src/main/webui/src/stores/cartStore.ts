import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import type { CartItem } from '@/types/cart'
import type { ProductResponse } from '@/api/generated/model'

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const venteId = ref<number | null>(null)

  const total = computed(() =>
    items.value.reduce((sum, item) => sum + item.price * item.quantity, 0),
  )

  const itemCount = computed(() =>
    items.value.reduce((count, item) => count + item.quantity, 0),
  )

  const isEmpty = computed(() => items.value.length === 0)

  function setVenteId(id: number) {
    if (venteId.value !== null && venteId.value !== id) {
      clearCart()
    }
    venteId.value = id
  }

  function addItem(product: ProductResponse) {
    const existing = items.value.find((item) => item.productId === product.id)
    if (existing) {
      existing.quantity++
    } else {
      items.value.push({
        productId: product.id,
        name: product.name,
        price: product.price,
        supplier: product.supplier,
        quantity: 1,
      })
    }
  }

  function updateQuantity(productId: number, quantity: number) {
    const item = items.value.find((i) => i.productId === productId)
    if (item && quantity >= 1) {
      item.quantity = quantity
    }
  }

  function removeItem(productId: number) {
    items.value = items.value.filter((item) => item.productId !== productId)
  }

  function clearCart() {
    items.value = []
  }

  return { items, venteId, total, itemCount, isEmpty, setVenteId, addItem, updateQuantity, removeItem, clearCart }
})
