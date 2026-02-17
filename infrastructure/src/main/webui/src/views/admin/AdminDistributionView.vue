<script setup lang="ts">
import { ref, computed } from 'vue'
import { useVenteStore } from '@/stores/venteStore'
import { storeToRefs } from 'pinia'
import { useToast } from '@/composables/useToast'
import { useAdminOrdersQuery, usePickupMutation } from '@/composables/api/useAdminOrdersApi'
import { statusLabel, statusClasses } from '@/utils/order-formatters'
import EmptyState from '@/components/ui/EmptyState.vue'
import type { AdminOrderResponse } from '@/api/generated/model'

const toast = useToast()
const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)

const { data: pageData, isLoading: loading } = useAdminOrdersQuery(
  selectedVenteId,
  null,
  null,
  null,
  1000,
)
const orders = computed(() => pageData.value?.data ?? [])
const pickupMutation = usePickupMutation(selectedVenteId)

const searchQuery = ref('')
const selectedSlot = ref('')
const showUnpickedOnly = ref(false)
const pickingUp = ref(new Set<string>())

const uniqueSlots = computed(() => {
  const slots = new Set<string>()
  for (const order of orders.value ?? []) {
    slots.add(order.timeSlotLabel)
  }
  return Array.from(slots).sort()
})

const slotCounts = computed(() => {
  const counts = new Map<string, number>()
  for (const order of orders.value ?? []) {
    counts.set(order.timeSlotLabel, (counts.get(order.timeSlotLabel) ?? 0) + 1)
  }
  return counts
})

const filteredOrders = computed(() => {
  let result = orders.value ?? []

  if (showUnpickedOnly.value) {
    result = result.filter((o) => o.status === 'PAID')
  } else if (selectedSlot.value) {
    result = result.filter((o) => o.timeSlotLabel === selectedSlot.value)
  }

  if (searchQuery.value.trim()) {
    const query = searchQuery.value.trim().toLowerCase()
    result = result.filter(
      (o) =>
        o.customerLastName.toLowerCase().includes(query) ||
        o.customerFirstName.toLowerCase().includes(query),
    )
  }

  return result
})

function selectSlot(slot: string) {
  showUnpickedOnly.value = false
  selectedSlot.value = slot
}

function selectAll() {
  showUnpickedOnly.value = false
  selectedSlot.value = ''
}

function selectUnpicked() {
  selectedSlot.value = ''
  showUnpickedOnly.value = true
}

function handlePrint() {
  if (!selectedVenteId.value) return
  window.open(`/api/admin/ventes/${selectedVenteId.value}/orders/distribution-pdf`, '_blank')
}

async function handlePickup(orderId: string) {
  if (pickingUp.value.has(orderId)) return
  const orderList = orders.value ?? []
  const order = orderList.find((o) => o.id === orderId)
  if (!order) return

  pickingUp.value.add(orderId)
  const previousStatus = order.status
  ;(order as AdminOrderResponse).status = 'PICKED_UP'

  try {
    await pickupMutation.mutateAsync(orderId)
    toast.success('Commande marquée comme récupérée')
  } catch {
    ;(order as AdminOrderResponse).status = previousStatus
    toast.error('Erreur lors de la mise à jour de la commande')
  } finally {
    pickingUp.value.delete(orderId)
  }
}
</script>

<template>
  <div>
    <div class="mb-6 flex items-center justify-between">
      <h1 class="text-2xl font-bold text-dark" data-testid="distribution-title">Distribution</h1>
      <button
        class="inline-flex items-center gap-2 rounded-lg bg-primary px-4 py-2 text-sm font-medium text-dark shadow-sm hover:bg-primary/90 transition-colors"
        data-testid="distribution-print-btn"
        @click="handlePrint"
      >
        <svg
          class="h-4 w-4"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M6.72 13.829c-.24.03-.48.062-.72.096m.72-.096a42.415 42.415 0 0 1 10.56 0m-10.56 0L6.34 18m10.94-4.171c.24.03.48.062.72.096m-.72-.096L17.66 18m0 0 .229 2.523a1.125 1.125 0 0 1-1.12 1.227H7.231c-.662 0-1.18-.568-1.12-1.227L6.34 18m11.318 0h1.091A2.25 2.25 0 0 0 21 15.75V9.456c0-1.081-.768-2.015-1.837-2.175a48.055 48.055 0 0 0-1.913-.247M6.34 18H5.25A2.25 2.25 0 0 1 3 15.75V9.456c0-1.081.768-2.015 1.837-2.175a48.041 48.041 0 0 1 1.913-.247m10.5 0a48.536 48.536 0 0 0-10.5 0m10.5 0V3.375c0-.621-.504-1.125-1.125-1.125h-8.25c-.621 0-1.125.504-1.125 1.125v3.659M18.25 7.034V3.375"
          />
        </svg>
        Imprimer PDF
      </button>
    </div>

    <!-- Slot Filter -->
    <div class="mb-4 flex flex-wrap gap-2" data-testid="distribution-slot-filter">
      <button
        :class="[
          'rounded-lg px-3 py-2 text-sm font-medium transition-colors',
          !selectedSlot && !showUnpickedOnly
            ? 'bg-primary text-dark'
            : 'border border-gray-300 bg-white text-dark hover:bg-gray-50',
        ]"
        data-testid="distribution-slot-all-btn"
        @click="selectAll"
      >
        Toutes ({{ (orders ?? []).length }})
      </button>
      <button
        v-for="slot in uniqueSlots"
        :key="slot"
        :class="[
          'rounded-lg px-3 py-2 text-sm font-medium transition-colors',
          selectedSlot === slot && !showUnpickedOnly
            ? 'bg-primary text-dark'
            : 'border border-gray-300 bg-white text-dark hover:bg-gray-50',
        ]"
        data-testid="distribution-slot-btn"
        @click="selectSlot(slot)"
      >
        {{ slot }} ({{ slotCounts.get(slot) ?? 0 }})
      </button>
      <button
        :class="[
          'rounded-lg px-3 py-2 text-sm font-medium transition-colors',
          showUnpickedOnly
            ? 'bg-primary text-dark'
            : 'border border-gray-300 bg-white text-dark hover:bg-gray-50',
        ]"
        data-testid="distribution-unpicked-btn"
        @click="selectUnpicked"
      >
        Non récupérées
      </button>
    </div>

    <!-- Search -->
    <div class="mb-4">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Rechercher par nom..."
        class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary sm:w-72"
        data-testid="distribution-search"
      />
    </div>

    <!-- Loading -->
    <div v-if="loading" class="py-12 text-center text-brown">Chargement des commandes...</div>

    <!-- Empty state -->
    <EmptyState
      v-else-if="!orders || orders.length === 0"
      message="Aucune commande pour le moment"
      data-testid="distribution-empty"
    />

    <!-- Orders list -->
    <div v-else>
      <p class="mb-3 text-sm text-brown" data-testid="distribution-order-count">
        {{ filteredOrders.length }} commande{{ filteredOrders.length > 1 ? 's' : '' }}
      </p>

      <div
        v-if="filteredOrders.length === 0"
        class="rounded-xl border border-gray-200 bg-white p-8 text-center"
      >
        <p class="text-brown">Aucune commande ne correspond à votre recherche.</p>
      </div>

      <div class="space-y-2">
        <div
          v-for="order in filteredOrders"
          :key="order.id"
          class="flex items-center justify-between rounded-lg border border-gray-200 bg-white px-4 py-3"
          data-testid="distribution-order-row"
        >
          <div class="flex-1">
            <span class="font-medium text-dark" data-testid="distribution-order-name">
              {{ order.customerLastName }} {{ order.customerFirstName }}
            </span>
            <span class="ml-2 text-sm text-brown">{{ order.orderNumber }}</span>
            <span class="ml-2 text-sm text-brown">{{ order.timeSlotLabel }}</span>
          </div>
          <div class="flex items-center gap-3">
            <span
              :class="[
                'inline-block rounded-full px-2 py-0.5 text-xs font-medium',
                statusClasses(order.status),
              ]"
              data-testid="distribution-order-status"
            >
              {{ statusLabel(order.status) }}
            </span>
            <button
              v-if="order.status === 'PAID'"
              class="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-dark shadow-sm transition-colors hover:bg-primary/90"
              data-testid="distribution-pickup-btn"
              @click="handlePickup(order.id)"
            >
              Marquer récupéré
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
