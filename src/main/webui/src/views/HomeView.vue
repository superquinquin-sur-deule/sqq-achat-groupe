<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useApi } from '@/composables/useApi'
import { useToast } from '@/composables/useToast'
import { useCartStore } from '@/stores/cartStore'
import { fetchProducts } from '@/api/products'
import { fetchVente } from '@/api/ventes'
import type { Product } from '@/types/product'
import type { Vente } from '@/types/vente'
import WelcomeBanner from '@/components/catalog/WelcomeBanner.vue'
import ProductGrid from '@/components/catalog/ProductGrid.vue'

const route = useRoute()
const venteId = Number(route.params.venteId)

const { data, loading, error, execute } = useApi(() => fetchProducts(venteId))
const toast = useToast()
const cartStore = useCartStore()
loading.value = true

const vente = ref<Vente | null>(null)
const venteClosed = computed(() => vente.value?.status === 'CLOSED')

cartStore.setVenteId(venteId)

const products = computed<Product[]>(() => (data as { value: Product[] | null }).value ?? [])

function handleAddToCart(product: Product) {
  cartStore.addItem(product)
  toast.success('Produit ajouté')
}

onMounted(async () => {
  try {
    vente.value = await fetchVente(venteId)
  } catch {
    // Vente not found — don't try loading products
    return
  }

  if (!venteClosed.value) {
    await execute()
    if (error.value) {
      toast.error('Impossible de charger le catalogue')
    }
  }
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
