<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { fetchBackofficeOrderDetail } from '@/api/backoffice'
import { useToast } from '@/composables/useToast'
import { formatPrice, statusLabel, statusClasses } from '@/utils/order-formatters'
import type { BackofficeOrderDetail } from '@/types/backoffice'

const route = useRoute()
const toast = useToast()

const order = ref<BackofficeOrderDetail | null>(null)
const loading = ref(false)

async function loadOrder() {
  const orderId = Number(route.params.id)
  loading.value = true
  try {
    order.value = await fetchBackofficeOrderDetail(orderId)
  } catch {
    toast.error('Erreur lors du chargement de la commande')
  } finally {
    loading.value = false
  }
}

onMounted(loadOrder)

function formatSlot(slot: { date: string; startTime: string; endTime: string } | null): string {
  if (!slot) return 'Créneau inconnu'
  return `${slot.date} ${slot.startTime}-${slot.endTime}`
}
</script>

<template>
  <div>
    <div class="mb-6">
      <RouterLink to="/backoffice/orders" class="text-sm text-brown hover:text-dark">
        &larr; Retour aux commandes
      </RouterLink>
    </div>

    <div v-if="loading" class="py-12 text-center text-brown">
      Chargement de la commande...
    </div>

    <div v-else-if="order" class="space-y-6">
      <div class="flex items-center gap-4">
        <h1 class="text-2xl font-bold text-dark">{{ order.orderNumber }}</h1>
        <span :class="['inline-block rounded-full px-3 py-1 text-sm font-medium', statusClasses(order.status)]">
          {{ statusLabel(order.status) }}
        </span>
      </div>

      <!-- Coordonnées -->
      <div class="rounded-xl border border-gray-200 bg-white p-6" data-testid="order-detail-customer">
        <h2 class="mb-4 text-lg font-semibold text-dark">Coordonnées</h2>
        <dl class="grid grid-cols-1 gap-3 sm:grid-cols-3">
          <div>
            <dt class="text-sm text-brown">Nom</dt>
            <dd class="font-medium text-dark">{{ order.customerName }}</dd>
          </div>
          <div>
            <dt class="text-sm text-brown">Email</dt>
            <dd class="font-medium text-dark">{{ order.customerEmail }}</dd>
          </div>
          <div>
            <dt class="text-sm text-brown">Téléphone</dt>
            <dd class="font-medium text-dark">{{ order.customerPhone }}</dd>
          </div>
        </dl>
      </div>

      <!-- Créneau -->
      <div class="rounded-xl border border-gray-200 bg-white p-6" data-testid="order-detail-timeslot">
        <h2 class="mb-4 text-lg font-semibold text-dark">Créneau de retrait</h2>
        <p class="font-medium text-dark">{{ formatSlot(order.timeSlot) }}</p>
      </div>

      <!-- Produits -->
      <div class="overflow-x-auto rounded-xl border border-gray-200 bg-white" data-testid="order-detail-items">
        <h2 class="px-6 pt-6 text-lg font-semibold text-dark">Produits commandés</h2>
        <table class="mt-4 w-full">
          <caption class="sr-only">Produits de la commande</caption>
          <thead>
            <tr class="bg-dark text-left text-sm text-white">
              <th class="px-6 py-3 font-medium">Produit</th>
              <th class="px-6 py-3 font-medium">Quantité</th>
              <th class="px-6 py-3 font-medium">Prix unitaire</th>
              <th class="px-6 py-3 font-medium">Sous-total</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in order.items" :key="index" class="border-t border-gray-100">
              <td class="px-6 py-3 font-medium text-dark">{{ item.productName }}</td>
              <td class="px-6 py-3 text-dark">{{ item.quantity }}</td>
              <td class="px-6 py-3 text-dark">{{ formatPrice(item.unitPrice) }}</td>
              <td class="px-6 py-3 text-dark">{{ formatPrice(item.subtotal) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Total -->
      <div class="rounded-xl border border-gray-200 bg-white p-6" data-testid="order-detail-total">
        <div class="flex items-center justify-between">
          <span class="text-lg font-semibold text-dark">Total</span>
          <span class="text-2xl font-bold text-dark">{{ formatPrice(order.totalAmount) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
