<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { getApiAdminSupplierOrders } from '@/api/generated/admin-supplier-orders/admin-supplier-orders'
import { useVenteStore } from '@/stores/venteStore'
import { storeToRefs } from 'pinia'
import { useToast } from '@/composables/useToast'
import type { SupplierOrderLineResponse } from '@/api/generated/model'

const toast = useToast()
const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)

const lines = ref<SupplierOrderLineResponse[]>([])
const loading = ref(false)

const groupedBySupplier = computed(() => {
  const groups = new Map<string, SupplierOrderLineResponse[]>()
  for (const line of lines.value) {
    const existing = groups.get(line.supplier)
    if (existing) {
      existing.push(line)
    } else {
      groups.set(line.supplier, [line])
    }
  }
  return Array.from(groups.entries()).sort(([a], [b]) => a.localeCompare(b))
})

const todayFormatted = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('fr-FR', { year: 'numeric', month: '2-digit', day: '2-digit' })
})

function handlePrint() {
  window.print()
}

async function handleExportXls() {
  if (!selectedVenteId.value) return
  const response = await fetch(`/api/admin/supplier-orders/xlsx?venteId=${selectedVenteId.value}`)
  const blob = await response.blob()
  const disposition = response.headers.get('Content-Disposition')
  const match = disposition?.match(/filename="?(.+?)"?$/)
  const filename = match?.[1] ?? `bon-fournisseur-${new Date().toISOString().slice(0, 10)}.xlsx`
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

async function loadData(venteId: number) {
  loading.value = true
  try {
    const response = await getApiAdminSupplierOrders({ venteId })
    lines.value = response.data.data ?? []
  } catch {
    toast.error('Erreur lors du chargement du bon fournisseur')
  } finally {
    loading.value = false
  }
}

watch(selectedVenteId, (id) => { if (id) loadData(id) }, { immediate: true })
</script>

<template>
  <div>
    <!-- Titre imprimé (masqué à l'écran, visible à l'impression) -->
    <h1
      class="hidden print:block print:mb-4 print:text-xl print:font-bold print:text-black"
      data-testid="supplier-order-print-title"
    >
      SuperQuinquin — Bon de commande fournisseur — {{ todayFormatted }}
    </h1>

    <!-- En-tête écran -->
    <div class="mb-6 flex items-center justify-between print:hidden">
      <h1 class="text-2xl font-bold text-dark" data-testid="supplier-order-title">
        Bon de commande fournisseur
      </h1>
      <div class="flex gap-2">
        <button
          class="inline-flex items-center gap-2 rounded-lg bg-primary px-4 py-2 text-sm font-medium text-dark shadow-sm hover:bg-primary/90 transition-colors"
          data-testid="supplier-order-print-btn"
          @click="handlePrint"
        >
          <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M6.72 13.829c-.24.03-.48.062-.72.096m.72-.096a42.415 42.415 0 0 1 10.56 0m-10.56 0L6.34 18m10.94-4.171c.24.03.48.062.72.096m-.72-.096L17.66 18m0 0 .229 2.523a1.125 1.125 0 0 1-1.12 1.227H7.231c-.662 0-1.18-.568-1.12-1.227L6.34 18m11.318 0h1.091A2.25 2.25 0 0 0 21 15.75V9.456c0-1.081-.768-2.015-1.837-2.175a48.055 48.055 0 0 0-1.913-.247M6.34 18H5.25A2.25 2.25 0 0 1 3 15.75V9.456c0-1.081.768-2.015 1.837-2.175a48.041 48.041 0 0 1 1.913-.247m10.5 0a48.536 48.536 0 0 0-10.5 0m10.5 0V3.375c0-.621-.504-1.125-1.125-1.125h-8.25c-.621 0-1.125.504-1.125 1.125v3.659M18.25 7.034V3.375" /></svg>
          Imprimer
        </button>
        <button
          class="inline-flex items-center gap-2 rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-dark shadow-sm hover:bg-gray-50 transition-colors"
          data-testid="supplier-order-export-btn"
          @click="handleExportXls"
        >
          <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M3 16.5v2.25A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75V16.5M16.5 12 12 16.5m0 0L7.5 12m4.5 4.5V3" /></svg>
          Exporter Excel
        </button>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="py-12 text-center text-brown print:hidden">
      Chargement du bon fournisseur...
    </div>

    <!-- État vide -->
    <div
      v-else-if="lines.length === 0"
      class="rounded-xl border border-gray-200 bg-white p-12 text-center"
      data-testid="supplier-order-empty"
    >
      <p class="text-brown">Aucune commande pour le moment</p>
    </div>

    <!-- Tableau groupé par fournisseur -->
    <div v-else class="overflow-x-auto rounded-xl border border-gray-200 bg-white print:border-0 print:rounded-none">
      <table class="w-full" data-testid="supplier-order-table">
        <caption class="sr-only">Bon de commande fournisseur</caption>
        <thead>
          <tr class="bg-dark text-left text-sm text-white print:bg-white print:text-black print:border-b-2 print:border-black">
            <th class="px-4 py-3 font-medium">Produit</th>
            <th class="px-4 py-3 font-medium text-right">Quantité totale</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="[supplier, supplierLines] in groupedBySupplier" :key="supplier">
            <tr data-testid="supplier-group" class="print:break-inside-avoid">
              <td
                colspan="2"
                class="bg-surface px-4 py-2 text-sm font-bold text-dark print:bg-white print:border-t print:border-gray-400"
                data-testid="supplier-group-header"
              >
                {{ supplier }}
              </td>
            </tr>
            <tr
              v-for="line in supplierLines"
              :key="line.productName"
              class="hover:bg-yellow-50 transition-colors print:hover:bg-white"
              data-testid="supplier-order-row"
            >
              <td class="border-t border-gray-100 px-4 py-2 text-dark print:border-gray-300">
                {{ line.productName }}
              </td>
              <td class="border-t border-gray-100 px-4 py-2 text-right text-dark print:border-gray-300">
                {{ line.totalQuantity }}
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </div>
</template>
