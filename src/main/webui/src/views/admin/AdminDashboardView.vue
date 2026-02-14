<script setup lang="ts">
import { ref, watch } from 'vue'
import StatCard from '@/components/admin/StatCard.vue'
import Card from '@/components/ui/Card.vue'
import { getApiAdminDashboard } from '@/api/generated/admin-dashboard/admin-dashboard'
import { useVenteStore } from '@/stores/venteStore'
import { storeToRefs } from 'pinia'
import { useToast } from '@/composables/useToast'
import type { DashboardStatsResponse } from '@/api/generated/model'

const toast = useToast()
const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)

const stats = ref<DashboardStatsResponse | null>(null)
const loading = ref(false)

function formatCurrency(value: number): string {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(value)
}

async function loadData(venteId: number) {
  loading.value = true
  try {
    const response = await getApiAdminDashboard({ venteId })
    stats.value = response.data.data ?? null
  } catch {
    toast.error('Erreur lors du chargement des statistiques')
  } finally {
    loading.value = false
  }
}

watch(selectedVenteId, (id) => { if (id) loadData(id) }, { immediate: true })
</script>

<template>
  <div>
    <h1 class="mb-6 text-2xl font-bold text-dark">Tableau de bord</h1>

    <div v-if="loading" class="py-12 text-center text-brown">
      Chargement des statistiques...
    </div>

    <template v-else-if="stats">
      <div data-testid="dashboard-stats" class="mb-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard
          data-testid="stat-total-orders"
          label="Commandes"
          :value="stats.totalOrders"
        />
        <StatCard
          data-testid="stat-total-amount"
          label="Montant total"
          :value="formatCurrency(stats.totalAmount)"
        />
        <StatCard
          data-testid="stat-pickup-rate"
          label="Taux de retrait"
          :value="stats.pickupRate"
          unit="%"
        />
        <StatCard
          data-testid="stat-average-basket"
          label="Panier moyen"
          :value="formatCurrency(stats.averageBasket)"
        />
      </div>

      <!-- Podium top 3 produits -->
      <Card v-if="stats.topProducts.length > 0" class="mb-8">
        <h2 class="mb-6 text-center text-lg font-semibold text-dark">Top produits vendus</h2>
        <div class="flex items-end justify-center gap-3">
          <!-- 2e place -->
          <div v-if="stats.topProducts.length >= 2" class="flex w-28 flex-col items-center">
            <p class="mb-2 truncate text-center text-sm font-medium text-dark" :title="stats.topProducts[1]?.productName">
              {{ stats.topProducts[1]?.productName }}
            </p>
            <div class="flex h-24 w-full items-end justify-center rounded-t-lg bg-brown/20">
              <span class="pb-2 text-lg font-bold text-brown">{{ stats.topProducts[1]?.totalQuantity }}</span>
            </div>
            <div class="w-full rounded-b-lg bg-brown py-1 text-center text-sm font-bold text-white">2e</div>
          </div>
          <!-- 1ere place -->
          <div class="flex w-28 flex-col items-center">
            <p class="mb-2 truncate text-center text-sm font-medium text-dark" :title="stats.topProducts[0]?.productName">
              {{ stats.topProducts[0]?.productName }}
            </p>
            <div class="flex h-36 w-full items-end justify-center rounded-t-lg bg-primary/20">
              <span class="pb-2 text-xl font-bold text-primary">{{ stats.topProducts[0]?.totalQuantity }}</span>
            </div>
            <div class="w-full rounded-b-lg bg-primary py-1 text-center text-sm font-bold text-white">1er</div>
          </div>
          <!-- 3e place -->
          <div v-if="stats.topProducts.length >= 3" class="flex w-28 flex-col items-center">
            <p class="mb-2 truncate text-center text-sm font-medium text-dark" :title="stats.topProducts[2]?.productName">
              {{ stats.topProducts[2]?.productName }}
            </p>
            <div class="flex h-16 w-full items-end justify-center rounded-t-lg bg-dark/10">
              <span class="pb-2 text-lg font-bold text-dark">{{ stats.topProducts[2]?.totalQuantity }}</span>
            </div>
            <div class="w-full rounded-b-lg bg-dark py-1 text-center text-sm font-bold text-white">3e</div>
          </div>
        </div>
      </Card>

      <div class="overflow-x-auto rounded-xl border border-gray-200 bg-white">
        <table class="w-full" data-testid="slot-distribution-table">
          <caption class="sr-only">Répartition des commandes par créneau</caption>
          <thead>
            <tr class="bg-dark text-left text-sm text-white">
              <th class="px-4 py-3 font-medium">Créneau</th>
              <th class="px-4 py-3 font-medium">Nombre de commandes</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="slot in stats.slotDistribution"
              :key="slot.slotId"
              class="border-t border-gray-100 transition-colors hover:bg-surface"
              data-testid="slot-distribution-row"
            >
              <td class="px-4 py-3 text-dark">{{ slot.slotLabel }}</td>
              <td class="px-4 py-3 font-medium text-dark">{{ slot.orderCount }}</td>
            </tr>
            <tr v-if="stats.slotDistribution.length === 0">
              <td colspan="2" class="px-4 py-6 text-center text-brown">
                Aucune commande par créneau
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>

    <div v-else class="py-12 text-center text-brown">
      Aucune donnée disponible
    </div>
  </div>
</template>
