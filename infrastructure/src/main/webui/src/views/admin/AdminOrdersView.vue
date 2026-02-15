<script setup lang="ts">
import { ref, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useVenteStore } from '@/stores/venteStore'
import { storeToRefs } from 'pinia'
import { useAdminOrdersQuery } from '@/composables/api/useAdminOrdersApi'
import { formatPrice, statusLabel, statusClasses } from '@/utils/order-formatters'
import AdminTable from '@/components/admin/AdminTable.vue'

const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)

const { data: orders, isLoading: loading } = useAdminOrdersQuery(selectedVenteId)

const searchQuery = ref('')
const selectedSlot = ref('')

const uniqueSlots = computed(() => {
  const slots = new Set((orders.value ?? []).map((o) => o.timeSlotLabel))
  return Array.from(slots).sort()
})

const filteredOrders = computed(() => {
  let result = orders.value ?? []
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.trim().toLowerCase()
    result = result.filter((o) => o.customerName.toLowerCase().includes(query))
  }
  if (selectedSlot.value) {
    result = result.filter((o) => o.timeSlotLabel === selectedSlot.value)
  }
  return result
})
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

    <div v-if="loading" class="py-12 text-center text-brown">Chargement des commandes...</div>

    <div
      v-else-if="!orders || orders.length === 0"
      class="rounded-xl border border-gray-200 bg-white p-12 text-center"
      data-testid="empty-state"
    >
      <p class="text-brown">Aucune commande payée pour cette vente.</p>
    </div>

    <AdminTable
      v-else
      :columns="['Numéro', 'Nom', 'Email', 'Créneau', 'Montant', 'Statut']"
      caption="Liste des commandes"
      dataTestid="backoffice-orders-table"
    >
      <tr
        v-for="order in filteredOrders"
        :key="order.id"
        class="hover:bg-yellow-50 transition-colors"
        data-testid="backoffice-order-row"
      >
        <td class="border-t border-gray-100 px-4 py-3">
          <RouterLink
            :to="{ name: 'admin-order-detail', params: { id: order.id } }"
            class="font-medium text-dark underline decoration-primary/30 hover:decoration-primary"
          >
            {{ order.orderNumber }}
          </RouterLink>
        </td>
        <td class="border-t border-gray-100 px-4 py-3 text-dark">{{ order.customerName }}</td>
        <td class="border-t border-gray-100 px-4 py-3 text-dark">{{ order.customerEmail }}</td>
        <td class="border-t border-gray-100 px-4 py-3 text-dark">{{ order.timeSlotLabel }}</td>
        <td class="border-t border-gray-100 px-4 py-3 text-dark">
          {{ formatPrice(order.totalAmount) }}
        </td>
        <td class="border-t border-gray-100 px-4 py-3" data-testid="backoffice-order-status">
          <span
            :class="[
              'inline-block rounded-full px-2 py-0.5 text-xs font-medium',
              statusClasses(order.status),
            ]"
          >
            {{ statusLabel(order.status) }}
          </span>
        </td>
      </tr>
      <tr v-if="filteredOrders.length === 0">
        <td colspan="6" class="border-t border-gray-100 px-4 py-8 text-center text-brown">
          Aucune commande ne correspond à votre recherche.
        </td>
      </tr>
    </AdminTable>
  </div>
</template>
