<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useToast } from '@/composables/useToast'
import { useCartStore } from '@/stores/cartStore'
import { useVenteQuery } from '@/composables/api/useVentesApi'
import { useVenteProductsQuery } from '@/composables/api/useProductsApi'
import { useVenteTimeslotsQuery } from '@/composables/api/useTimeslotsApi'
import type { ProductResponse } from '@/api/generated/model'
import WelcomeBanner from '@/components/catalog/WelcomeBanner.vue'
import TimeSlotBanner from '@/components/catalog/TimeSlotBanner.vue'
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
      <WelcomeBanner />
      <TimeSlotBanner :time-slots="timeSlots ?? []" :is-loading="timeSlotsLoading" />
      <div class="py-8">
        <ProductGrid :products="products ?? []" :loading="loading" @add="handleAddToCart" />
      </div>
    </template>
  </section>
</template>
