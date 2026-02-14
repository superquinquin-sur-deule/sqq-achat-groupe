<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { getApiBackofficeOrders, putApiBackofficeOrdersIdPickup } from '@/api/generated/backoffice-orders/backoffice-orders'
import { useVenteStore } from '@/stores/venteStore'
import { storeToRefs } from 'pinia'
import { useToast } from '@/composables/useToast'
import { statusLabel, statusClasses } from '@/utils/order-formatters'
import type { BackofficeOrderResponse } from '@/api/generated/model'

const toast = useToast()
const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)

const orders = ref<BackofficeOrderResponse[]>([])
const loading = ref(false)
const searchQuery = ref('')
const selectedSlot = ref('')
const showUnpickedOnly = ref(false)
const pickingUp = ref(new Set<number>())

const uniqueSlots = computed(() => {
  const slots = new Set<string>()
  for (const order of orders.value) {
    slots.add(order.timeSlotLabel)
  }
  return Array.from(slots).sort()
})

const slotCounts = computed(() => {
  const counts = new Map<string, number>()
  for (const order of orders.value) {
    counts.set(order.timeSlotLabel, (counts.get(order.timeSlotLabel) ?? 0) + 1)
  }
  return counts
})

const filteredOrders = computed(() => {
  let result = orders.value

  if (showUnpickedOnly.value) {
    result = result.filter((o) => o.status === 'PAID')
  } else if (selectedSlot.value) {
    result = result.filter((o) => o.timeSlotLabel === selectedSlot.value)
  }

  if (searchQuery.value.trim()) {
    const query = searchQuery.value.trim().toLowerCase()
    result = result.filter((o) => o.customerName.toLowerCase().includes(query))
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

async function handlePickup(orderId: number) {
  if (pickingUp.value.has(orderId)) return
  const order = orders.value.find((o) => o.id === orderId)
  if (!order) return

  pickingUp.value.add(orderId)
  const previousStatus = order.status
  order.status = 'PICKED_UP'

  try {
    await putApiBackofficeOrdersIdPickup(orderId)
    toast.success('Commande marquée comme récupérée')
  } catch {
    order.status = previousStatus
    toast.error('Erreur lors de la mise à jour de la commande')
  } finally {
    pickingUp.value.delete(orderId)
  }
}

async function loadData(venteId: number) {
  loading.value = true
  try {
    const response = await getApiBackofficeOrders({ venteId })
    orders.value = response.data.data ?? []
  } catch {
    toast.error('Erreur lors du chargement des commandes')
  } finally {
    loading.value = false
  }
}

watch(selectedVenteId, (id) => { if (id) loadData(id) }, { immediate: true })
</script>

<template>
  <div>
    <h1 class="mb-6 text-2xl font-bold text-dark" data-testid="distribution-title">
      Distribution
    </h1>

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
        Toutes ({{ orders.length }})
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
    <div v-if="loading" class="py-12 text-center text-brown">
      Chargement des commandes...
    </div>

    <!-- Empty state -->
    <div
      v-else-if="orders.length === 0"
      class="rounded-xl border border-gray-200 bg-white p-12 text-center"
      data-testid="distribution-empty"
    >
      <p class="text-brown">Aucune commande pour le moment</p>
    </div>

    <!-- Orders list -->
    <div v-else>
      <p class="mb-3 text-sm text-brown" data-testid="distribution-order-count">
        {{ filteredOrders.length }} commande{{ filteredOrders.length > 1 ? 's' : '' }}
      </p>

      <div v-if="filteredOrders.length === 0" class="rounded-xl border border-gray-200 bg-white p-8 text-center">
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
              {{ order.customerName }}
            </span>
            <span class="ml-2 text-sm text-brown">{{ order.orderNumber }}</span>
            <span class="ml-2 text-sm text-brown">{{ order.timeSlotLabel }}</span>
          </div>
          <div class="flex items-center gap-3">
            <span
              :class="['inline-block rounded-full px-2 py-0.5 text-xs font-medium', statusClasses(order.status)]"
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
