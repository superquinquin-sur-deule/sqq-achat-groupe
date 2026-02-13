<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { fetchBackofficeOrders } from '@/api/backoffice'
import { fetchVentes } from '@/api/ventes'
import { useToast } from '@/composables/useToast'
import { formatPrice, statusLabel, statusClasses } from '@/utils/order-formatters'
import type { BackofficeOrder } from '@/types/backoffice'

const toast = useToast()

const orders = ref<BackofficeOrder[]>([])
const loading = ref(false)
const searchQuery = ref('')
const selectedSlot = ref('')

const uniqueSlots = computed(() => {
  const slots = new Set(orders.value.map((o) => o.timeSlotLabel))
  return Array.from(slots).sort()
})

const filteredOrders = computed(() => {
  let result = orders.value
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.trim().toLowerCase()
    result = result.filter((o) => o.customerName.toLowerCase().includes(query))
  }
  if (selectedSlot.value) {
    result = result.filter((o) => o.timeSlotLabel === selectedSlot.value)
  }
  return result
})

async function loadData() {
  loading.value = true
  try {
    const ventes = await fetchVentes()
    const firstVente = ventes[0]
    if (firstVente) {
      orders.value = await fetchBackofficeOrders(firstVente.id)
    }
  } catch {
    toast.error('Erreur lors du chargement des commandes')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <div>
    <h1 class="mb-6 text-2xl font-bold text-dark">Commandes</h1>

    <div class="mb-4 flex flex-col gap-3 sm:flex-row sm:items-center">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Rechercher par nom..."
        class="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary"
        data-testid="backoffice-search-input"
      />
      <select
        v-model="selectedSlot"
        class="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary"
        data-testid="backoffice-slot-filter"
      >
        <option value="">Tous les créneaux</option>
        <option v-for="slot in uniqueSlots" :key="slot" :value="slot">{{ slot }}</option>
      </select>
    </div>

    <div v-if="loading" class="py-12 text-center text-brown">
      Chargement des commandes...
    </div>

    <div v-else-if="orders.length === 0" class="rounded-xl border border-gray-200 bg-white p-12 text-center" data-testid="empty-state">
      <p class="text-brown">Aucune commande payée pour cette vente.</p>
    </div>

    <div v-else class="overflow-x-auto rounded-xl border border-gray-200 bg-white">
      <table class="w-full" data-testid="backoffice-orders-table">
        <caption class="sr-only">Liste des commandes</caption>
        <thead>
          <tr class="bg-dark text-left text-sm text-white">
            <th class="px-4 py-3 font-medium">Numéro</th>
            <th class="px-4 py-3 font-medium">Nom</th>
            <th class="px-4 py-3 font-medium">Email</th>
            <th class="px-4 py-3 font-medium">Créneau</th>
            <th class="px-4 py-3 font-medium">Montant</th>
            <th class="px-4 py-3 font-medium">Statut</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="order in filteredOrders"
            :key="order.id"
            class="hover:bg-yellow-50 transition-colors"
            data-testid="backoffice-order-row"
          >
            <td class="border-t border-gray-100 px-4 py-3">
              <RouterLink
                :to="`/admin/orders/${order.id}`"
                class="font-medium text-dark underline decoration-primary/30 hover:decoration-primary"
              >
                {{ order.orderNumber }}
              </RouterLink>
            </td>
            <td class="border-t border-gray-100 px-4 py-3 text-dark">{{ order.customerName }}</td>
            <td class="border-t border-gray-100 px-4 py-3 text-dark">{{ order.customerEmail }}</td>
            <td class="border-t border-gray-100 px-4 py-3 text-dark">{{ order.timeSlotLabel }}</td>
            <td class="border-t border-gray-100 px-4 py-3 text-dark">{{ formatPrice(order.totalAmount) }}</td>
            <td class="border-t border-gray-100 px-4 py-3" data-testid="backoffice-order-status">
              <span :class="['inline-block rounded-full px-2 py-0.5 text-xs font-medium', statusClasses(order.status)]">
                {{ statusLabel(order.status) }}
              </span>
            </td>
          </tr>
          <tr v-if="filteredOrders.length === 0">
            <td colspan="6" class="border-t border-gray-100 px-4 py-8 text-center text-brown">
              Aucune commande ne correspond à votre recherche.
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
