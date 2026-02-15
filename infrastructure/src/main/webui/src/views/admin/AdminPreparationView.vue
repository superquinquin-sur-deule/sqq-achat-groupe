<script setup lang="ts">
import { ref, computed } from 'vue'
import { useVenteStore } from '@/stores/venteStore'
import { storeToRefs } from 'pinia'
import { useAdminPreparationQuery } from '@/composables/api/useAdminPreparationApi'
import EmptyState from '@/components/ui/EmptyState.vue'

const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)

const { data: orders, isLoading: loading } = useAdminPreparationQuery(selectedVenteId)

const selectedSlot = ref('')

const uniqueSlots = computed(() => {
  const slots = new Set<string>()
  for (const order of orders.value ?? []) {
    slots.add(order.timeSlotLabel)
  }
  return Array.from(slots).sort()
})

const filteredOrders = computed(() => {
  if (!selectedSlot.value) {
    return orders.value ?? []
  }
  return (orders.value ?? []).filter((o) => o.timeSlotLabel === selectedSlot.value)
})

const groupedBySlot = computed(() => {
  const groups = new Map<string, typeof filteredOrders.value>()
  for (const order of filteredOrders.value) {
    const existing = groups.get(order.timeSlotLabel)
    if (existing) {
      existing.push(order)
    } else {
      groups.set(order.timeSlotLabel, [order])
    }
  }
  return Array.from(groups.entries()).sort(([a], [b]) => a.localeCompare(b))
})

const lastOrderId = computed(() => {
  const entries = groupedBySlot.value
  if (entries.length === 0) return null
  const lastEntry = entries[entries.length - 1]
  if (!lastEntry) return null
  const lastSlotOrders = lastEntry[1]
  return lastSlotOrders[lastSlotOrders.length - 1]?.orderId ?? null
})

const todayFormatted = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('fr-FR', { year: 'numeric', month: '2-digit', day: '2-digit' })
})

function handlePrint() {
  if (!selectedVenteId.value) return
  window.open(`/api/admin/preparation/pdf?venteId=${selectedVenteId.value}`, '_blank')
}
</script>

<template>
  <div>
    <!-- Titre imprimé (masqué à l'écran, visible à l'impression) -->
    <h1
      class="hidden print:block print:mb-4 print:text-xl print:font-bold print:text-black"
      data-testid="preparation-print-title"
    >
      SuperQuinquin — Listes de préparation — {{ todayFormatted }}
    </h1>

    <!-- En-tête écran -->
    <div class="mb-6 flex items-center justify-between print:hidden">
      <h1 class="text-2xl font-bold text-dark" data-testid="preparation-title">
        Listes de préparation
      </h1>
      <div class="flex items-center gap-3">
        <select
          v-model="selectedSlot"
          class="rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm text-dark shadow-sm"
          data-testid="preparation-slot-filter"
        >
          <option value="">Tous les créneaux</option>
          <option v-for="slot in uniqueSlots" :key="slot" :value="slot">
            {{ slot }}
          </option>
        </select>
        <button
          class="inline-flex items-center gap-2 rounded-lg bg-primary px-4 py-2 text-sm font-medium text-dark shadow-sm hover:bg-primary/90 transition-colors"
          data-testid="preparation-print-btn"
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
          Imprimer les listes
        </button>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="py-12 text-center text-brown print:hidden">
      Chargement des listes de préparation...
    </div>

    <!-- État vide -->
    <EmptyState
      v-else-if="!orders || orders.length === 0"
      message="Aucune commande pour le moment"
      data-testid="preparation-empty"
    />

    <!-- Fiches de préparation groupées par créneau -->
    <div v-else>
      <template v-for="[slot, slotOrders] in groupedBySlot" :key="slot">
        <div data-testid="preparation-slot-section" class="mb-6">
          <!-- En-tête de section créneau -->
          <div
            class="mb-3 rounded-t-lg bg-surface px-4 py-2 text-sm font-bold text-dark print:bg-white print:border-b print:border-gray-400 print:rounded-none"
            data-testid="preparation-slot-header"
          >
            {{ slot }}
          </div>

          <!-- Fiches de commande -->
          <div
            v-for="order in slotOrders"
            :key="order.orderId"
            :class="[
              'mb-4 rounded-lg border border-gray-200 bg-white p-4 print:rounded-none print:border-gray-300 print:mb-0 print:break-inside-avoid',
              order.orderId !== lastOrderId ? 'print:break-after-page' : '',
            ]"
            data-testid="preparation-card"
          >
            <div class="mb-3 flex items-baseline justify-between">
              <h3 class="text-lg font-bold text-dark" data-testid="preparation-card-customer">
                {{ order.customerName }}
              </h3>
              <span class="text-sm text-brown" data-testid="preparation-card-order-number">
                {{ order.orderNumber }}
              </span>
            </div>
            <p class="mb-3 text-sm text-brown print:text-black">
              Créneau : {{ order.timeSlotLabel }}
            </p>
            <table class="w-full text-sm" data-testid="preparation-card-items">
              <thead>
                <tr class="border-b border-gray-200 text-left print:border-gray-400">
                  <th class="pb-1 font-medium text-dark">Produit</th>
                  <th class="pb-1 text-right font-medium text-dark">Quantité</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="item in order.items"
                  :key="item.productName"
                  class="border-b border-gray-100 print:border-gray-300"
                >
                  <td class="py-1 text-dark">{{ item.productName }}</td>
                  <td class="py-1 text-right text-dark">{{ item.quantity }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>
