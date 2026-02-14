<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useToast } from '@/composables/useToast'
import { useCartStore } from '@/stores/cartStore'
import { getApiVentesVenteIdProducts } from '@/api/generated/products/products'
import { getApiVentesVenteId } from '@/api/generated/ventes/ventes'
import type { ProductResponse, VenteResponse } from '@/api/generated/model'
import WelcomeBanner from '@/components/catalog/WelcomeBanner.vue'
import ProductGrid from '@/components/catalog/ProductGrid.vue'

const route = useRoute()
const venteId = Number(route.params.venteId)

const toast = useToast()
const cartStore = useCartStore()
const loading = ref(true)
const error = ref<string | null>(null)

const vente = ref<VenteResponse | null>(null)
const venteClosed = computed(() => vente.value?.status === 'CLOSED')

cartStore.setVenteId(venteId)

const products = ref<ProductResponse[]>([])

function handleAddToCart(product: ProductResponse) {
  cartStore.addItem(product)
  toast.success('Produit ajouté')
}

onMounted(async () => {
  try {
    const venteResponse = await getApiVentesVenteId(venteId)
    vente.value = venteResponse.data.data ?? null
  } catch {
    // Vente not found — don't try loading products
    loading.value = false
    return
  }

  if (!venteClosed.value) {
    try {
      const productsResponse = await getApiVentesVenteIdProducts(venteId)
      products.value = productsResponse.data.data ?? []
    } catch {
      error.value = 'Impossible de charger le catalogue'
      toast.error('Impossible de charger le catalogue')
    }
  }
  loading.value = false
})
</script>

<template>
  <section aria-label="Catalogue des produits">
    <div v-if="venteClosed" class="mx-auto max-w-2xl px-4 py-16 text-center">
      <h1 class="mb-4 text-2xl font-bold text-dark">Les commandes ne sont pas encore ouvertes</h1>
      <p class="text-brown">Revenez plus tard lorsque la période de commande sera activée.</p>
    </div>
    <template v-else>
      <WelcomeBanner />
      <div class="py-8">
        <ProductGrid :products="products" :loading="loading" @add="handleAddToCart" />
      </div>
    </template>
  </section>
</template>
