<script setup lang="ts">
import { ref, onMounted } from 'vue'
import StatCard from '@/components/admin/StatCard.vue'
import { fetchDashboardStats, fetchCampaignVentes } from '@/api/admin'
import { useToast } from '@/composables/useToast'
import type { DashboardStats } from '@/types/dashboard'

const toast = useToast()

const stats = ref<DashboardStats | null>(null)
const loading = ref(false)

function formatCurrency(value: number): string {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(value)
}

async function loadData() {
  loading.value = true
  try {
    const ventes = await fetchCampaignVentes()
    const firstVente = ventes[0]
    if (firstVente) {
      stats.value = await fetchDashboardStats(firstVente.id)
    }
  } catch {
    toast.error('Erreur lors du chargement des statistiques')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
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
