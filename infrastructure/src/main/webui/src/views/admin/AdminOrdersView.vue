<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { useVenteStore } from '@/stores/venteStore'
import { storeToRefs } from 'pinia'
import { useAdminOrdersQuery } from '@/composables/api/useAdminOrdersApi'
import { useAdminTimeslotsQuery } from '@/composables/api/useAdminTimeslotsApi'
import { useCursorPagination } from '@/composables/useCursorPagination'
import { formatPrice, statusLabel, statusClasses } from '@/utils/order-formatters'
import AdminTable from '@/components/admin/AdminTable.vue'
import EmptyState from '@/components/ui/EmptyState.vue'
import Pagination from '@/components/ui/Pagination.vue'

const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)
const pagination = useCursorPagination()

const searchInput = ref('')
const debouncedSearch = ref<string | null>(null)
const selectedTimeSlotId = ref<number | null>(null)
let searchTimeout: ReturnType<typeof setTimeout> | null = null

function onSearchInput() {
  if (searchTimeout) clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    debouncedSearch.value = searchInput.value.trim() || null
    pagination.reset()
  }, 300)
}

watch(selectedVenteId, () => {
  pagination.reset()
  searchInput.value = ''
  debouncedSearch.value = null
  selectedTimeSlotId.value = null
})

watch(selectedTimeSlotId, () => {
  pagination.reset()
})

const { data: pageData, isLoading: loading } = useAdminOrdersQuery(
  selectedVenteId,
  pagination.currentCursor,
  debouncedSearch,
  selectedTimeSlotId,
)
const orders = computed(() => pageData.value?.data ?? [])
const pageInfo = computed(() => pageData.value?.pageInfo ?? { endCursor: null, hasNext: false })

// Fetch timeslots separately for the filter dropdown
const { data: timeslotsPageData } = useAdminTimeslotsQuery(selectedVenteId)
const timeSlots = computed(() => timeslotsPageData.value?.data ?? [])
</script>

<template>
  <div>
    <h1 class="mb-6 text-2xl font-bold text-dark">Commandes</h1>

    <div class="mb-4 flex flex-col gap-3 sm:flex-row sm:items-center">
      <input
        v-model="searchInput"
        type="text"
        placeholder="Rechercher par nom..."
        class="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary"
        data-testid="backoffice-search-input"
        @input="onSearchInput"
      />
      <select
        v-model="selectedTimeSlotId"
        class="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary"
        data-testid="backoffice-slot-filter"
      >
        <option :value="null">Tous les créneaux</option>
        <option v-for="slot in timeSlots" :key="slot.id" :value="slot.id">
          {{ slot.date }} {{ slot.startTime?.substring(0, 5) }}-{{ slot.endTime?.substring(0, 5) }}
        </option>
      </select>
    </div>

    <div v-if="loading" class="py-12 text-center text-brown">Chargement des commandes...</div>

    <EmptyState
      v-else-if="!orders || orders.length === 0"
      message="Aucune commande payée pour cette vente."
    />

    <AdminTable
      v-else
      :columns="['Numéro', 'Nom', 'Email', 'Créneau', 'Montant', 'Statut']"
      caption="Liste des commandes"
      dataTestid="backoffice-orders-table"
    >
      <tr
        v-for="order in orders"
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
        <td class="border-t border-gray-100 px-4 py-3 text-dark">{{ order.customerLastName }} {{ order.customerFirstName }}</td>
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
    </AdminTable>

    <Pagination
      v-if="orders && orders.length > 0"
      :has-next="pageInfo.hasNext"
      :has-previous="pagination.hasPrevious.value"
      :page-number="pagination.pageNumber.value"
      :loading="loading"
      @next="pagination.goToNext(pageInfo.endCursor!)"
      @previous="pagination.goToPrevious()"
    />
  </div>
</template>
