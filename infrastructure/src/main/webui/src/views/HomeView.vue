<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useToast } from '@/composables/useToast'
import { useCartStore } from '@/stores/cartStore'
import { useVenteQuery } from '@/composables/api/useVentesApi'
import { useVenteProductsQuery } from '@/composables/api/useProductsApi'
import { useVenteTimeslotsQuery } from '@/composables/api/useTimeslotsApi'
import type { ProductResponse } from '@/api/generated/model'
import { getRayonColor } from '@/utils/rayon-colors'
import TimeSlotBanner from '@/components/catalog/TimeSlotBanner.vue'
import RayonNav from '@/components/catalog/RayonNav.vue'
import ProductGrid from '@/components/catalog/ProductGrid.vue'

const route = useRoute()
const venteId = Number(route.params.venteId)

const toast = useToast()
const cartStore = useCartStore()

cartStore.setVenteId(venteId)

const { data: vente } = useVenteQuery(venteId)
const venteClosed = computed(() => vente.value?.status === 'CLOSED')

const { data: products, isLoading: loading } = useVenteProductsQuery(
  venteId,
  computed(() => !venteClosed.value),
)

const { data: timeSlots, isLoading: timeSlotsLoading } = useVenteTimeslotsQuery(venteId)

const groupedRayons = computed(() => {
  if (!products.value) return []
  const seen = new Map<string, number>()
  for (const product of products.value) {
    const cat = product.category || 'Autres'
    if (!seen.has(cat)) seen.set(cat, seen.size)
  }
  return Array.from(seen.entries()).map(([name, index]) => ({
    name,
    color: getRayonColor(index),
  }))
})

function handleAddToCart(product: ProductResponse) {
  cartStore.addItem(product)
  toast.success('Produit ajouté')
}
</script>

<template>
  <section aria-label="Catalogue des produits">
    <div v-if="venteClosed" class="mx-auto max-w-2xl px-4 py-16 text-center">
      <h1 class="mb-4 text-2xl font-bold text-dark">Les commandes ne sont pas encore ouvertes</h1>
      <p class="text-brown">Revenez plus tard lorsque la période de commande sera activée.</p>
    </div>
    <template v-else>
      <div class="bg-primary px-4 py-6 md:px-6 md:py-10">
        <div class="mx-auto max-w-4xl text-center">
          <h1
            data-testid="vente-header"
            class="mt-4 text-2xl font-extrabold tracking-tight text-dark md:mt-6 md:text-3xl lg:text-4xl"
          >
            {{ vente?.name }}
          </h1>
          <p v-if="vente?.description" class="mt-2 text-lg text-brown">{{ vente.description }}</p>
        </div>
      </div>
      <TimeSlotBanner :time-slots="timeSlots ?? []" :is-loading="timeSlotsLoading" />
      <RayonNav
        v-if="!loading && groupedRayons.length > 0"
        :rayons="groupedRayons.map((r) => r.name)"
        :colors="groupedRayons.map((r) => r.color)"
      />
      <div class="py-8">
        <ProductGrid :products="products ?? []" :loading="loading" @add="handleAddToCart" />
      </div>
    </template>
  </section>
</template>
