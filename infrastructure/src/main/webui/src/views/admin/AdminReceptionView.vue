<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useVenteStore } from '@/stores/venteStore'
import { storeToRefs } from 'pinia'
import { useToast } from '@/composables/useToast'
import EmptyState from '@/components/ui/EmptyState.vue'
import { customFetch } from '@/api/mutator/custom-fetch'

const toast = useToast()
const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)

// Types
interface ReceptionLine {
  productId: number
  productName: string
  orderedQuantity: number
  receivedQuantity: number
  shortage: number
}
interface SupplierStatus {
  supplier: string
  receptionId: number | null
  status: string
  lines: ReceptionLine[]
}
interface ReceptionStatus {
  suppliers: SupplierStatus[]
  allReceived: boolean
  hasRefunds: boolean
}
interface ShortageItem {
  productId: number
  productName: string
  supplier: string
  orderedQty: number
  receivedQty: number
  shortage: number
  affectedOrderCount: number
}
interface RefundOrder {
  orderId: string
  orderNumber: string
  customerName: string
  originalAmount: number
  adjustedAmount: number
  refundAmount: number
  refundStatus: string
}
interface RefundResult {
  totalProcessed: number
  totalSucceeded: number
  totalFailed: number
  totalAmountRefunded: number
}

// State
const loading = ref(false)
const receptionStatus = ref<ReceptionStatus | null>(null)
const shortagePreview = ref<ShortageItem[]>([])
const refundPreview = ref<RefundOrder[]>([])
const refundResult = ref<RefundResult | null>(null)

// Reception form state
const showReceptionForm = ref(false)
const currentSupplier = ref('')
const isEditing = ref(false)
const receptionForm = ref<
  { productId: number; productName: string; orderedQuantity: number; receivedQuantity: number }[]
>([])
const submitting = ref(false)

// Computed
const hasShortages = computed(() => shortagePreview.value.length > 0)

// API calls
async function fetchReceptionStatus() {
  if (!selectedVenteId.value) return
  loading.value = true
  try {
    const res: any = await customFetch(`/api/admin/ventes/${selectedVenteId.value}/receptions`, {
      method: 'GET',
    })
    receptionStatus.value = res.data.data
  } catch {
    toast.error('Erreur lors du chargement des réceptions')
  } finally {
    loading.value = false
  }
}

async function fetchShortagePreview() {
  if (!selectedVenteId.value) return
  try {
    const res: any = await customFetch(
      `/api/admin/ventes/${selectedVenteId.value}/receptions/shortages`,
      { method: 'GET' },
    )
    shortagePreview.value = res.data.data.items
  } catch {
    toast.error('Erreur lors du chargement des manques')
  }
}

async function fetchRefundPreview() {
  if (!selectedVenteId.value) return
  try {
    const res: any = await customFetch(
      `/api/admin/ventes/${selectedVenteId.value}/receptions/refunds`,
      { method: 'GET' },
    )
    refundPreview.value = res.data.data.orders
  } catch {
    toast.error('Erreur lors du chargement des remboursements')
  }
}

function openReceptionForm(supplier: SupplierStatus) {
  currentSupplier.value = supplier.supplier
  isEditing.value = supplier.status === 'COMPLETED'
  receptionForm.value = supplier.lines.map((l) => ({
    productId: l.productId,
    productName: l.productName,
    orderedQuantity: l.orderedQuantity,
    receivedQuantity: isEditing.value ? l.receivedQuantity : l.orderedQuantity,
  }))
  showReceptionForm.value = true
}

async function submitReception() {
  if (!selectedVenteId.value || submitting.value) return
  submitting.value = true
  try {
    await customFetch(`/api/admin/ventes/${selectedVenteId.value}/receptions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        supplier: currentSupplier.value,
        lines: receptionForm.value.map((l) => ({
          productId: l.productId,
          receivedQuantity: l.receivedQuantity,
        })),
      }),
    })
    toast.success(isEditing.value ? 'Réception modifiée' : 'Réception enregistrée')
    showReceptionForm.value = false
    await fetchReceptionStatus()
    if (receptionStatus.value?.allReceived) {
      await fetchShortagePreview()
      await fetchRefundPreview()
    }
  } catch {
    toast.error("Erreur lors de l'enregistrement de la réception")
  } finally {
    submitting.value = false
  }
}

async function applyAdjustments() {
  if (!selectedVenteId.value || submitting.value) return
  if (
    !confirm(
      'Voulez-vous vraiment appliquer les ajustements de rupture ? Cette action est irréversible.',
    )
  )
    return
  submitting.value = true
  try {
    await customFetch(`/api/admin/ventes/${selectedVenteId.value}/receptions/adjustments`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
    })
    toast.success('Ajustements appliqués')
    await fetchShortagePreview()
    await fetchRefundPreview()
  } catch {
    toast.error("Erreur lors de l'application des ajustements")
  } finally {
    submitting.value = false
  }
}

async function processRefunds() {
  if (!selectedVenteId.value || submitting.value) return
  if (!confirm('Voulez-vous vraiment lancer les remboursements Stripe ?')) return
  submitting.value = true
  try {
    const res: any = await customFetch(
      `/api/admin/ventes/${selectedVenteId.value}/receptions/refunds`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
      },
    )
    refundResult.value = res.data.data
    toast.success(`${res.data.data.totalSucceeded} remboursement(s) effectué(s)`)
    await fetchRefundPreview()
  } catch {
    toast.error('Erreur lors des remboursements')
  } finally {
    submitting.value = false
  }
}

function formatAmount(amount: number) {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(amount)
}

// Load data when vente changes
watch(
  selectedVenteId,
  async (id) => {
    if (id) {
      await fetchReceptionStatus()
      if (receptionStatus.value?.allReceived) {
        await fetchShortagePreview()
        await fetchRefundPreview()
      }
    }
  },
  { immediate: true },
)
</script>

<template>
  <div>
    <h1 class="mb-6 text-2xl font-bold text-dark" data-testid="reception-title">Réception</h1>

    <!-- Loading -->
    <div v-if="loading" class="py-12 text-center text-brown" data-testid="reception-loading">
      Chargement des réceptions...
    </div>

    <EmptyState
      v-else-if="!receptionStatus"
      message="Aucune donnée disponible"
      data-testid="reception-empty"
    />

    <template v-else>
      <!-- Step 1: Supplier reception status -->
      <div class="mb-8" data-testid="reception-suppliers">
        <h2 class="mb-4 text-lg font-semibold text-dark">Réceptions par fournisseur</h2>
        <div class="space-y-3">
          <EmptyState
            v-if="receptionStatus.suppliers.length === 0"
            message="Aucun fournisseur pour cette vente"
            data-testid="reception-no-suppliers"
          />
          <div
            v-for="supplier in receptionStatus.suppliers"
            :key="supplier.supplier"
            class="rounded-lg border border-gray-200 bg-white p-4"
            data-testid="reception-supplier-row"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3">
                <span class="font-medium text-dark" data-testid="reception-supplier-name">
                  {{ supplier.supplier }}
                </span>
                <span
                  :class="[
                    'inline-block rounded-full px-2 py-0.5 text-xs font-medium',
                    supplier.status === 'COMPLETED'
                      ? 'bg-green-100 text-green-800'
                      : 'bg-yellow-100 text-yellow-800',
                  ]"
                  data-testid="reception-supplier-status"
                >
                  {{ supplier.status === 'COMPLETED' ? 'Reçu' : 'En attente' }}
                </span>
              </div>
              <div class="flex gap-2">
                <button
                  v-if="supplier.status === 'PENDING'"
                  class="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-dark shadow-sm transition-colors hover:bg-primary/90"
                  data-testid="reception-record-btn"
                  @click="openReceptionForm(supplier)"
                >
                  Saisir réception
                </button>
                <button
                  v-if="supplier.status === 'COMPLETED' && !receptionStatus?.hasRefunds"
                  class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-dark hover:bg-gray-50"
                  data-testid="reception-edit-btn"
                  @click="openReceptionForm(supplier)"
                >
                  Modifier
                </button>
              </div>
            </div>

            <!-- Show reception details if completed -->
            <div v-if="supplier.status === 'COMPLETED' && supplier.lines.length > 0" class="mt-3">
              <table class="w-full text-sm" data-testid="reception-supplier-lines">
                <thead>
                  <tr class="border-b border-gray-200 text-left">
                    <th class="pb-1 font-medium text-dark">Produit</th>
                    <th class="pb-1 text-right font-medium text-dark">Commandé</th>
                    <th class="pb-1 text-right font-medium text-dark">Reçu</th>
                    <th class="pb-1 text-right font-medium text-dark">Manque</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="line in supplier.lines"
                    :key="line.productId"
                    :class="['border-b border-gray-100', line.shortage > 0 ? 'bg-red-50' : '']"
                  >
                    <td class="py-1 text-dark">{{ line.productName }}</td>
                    <td class="py-1 text-right text-dark">{{ line.orderedQuantity }}</td>
                    <td class="py-1 text-right text-dark">{{ line.receivedQuantity }}</td>
                    <td
                      class="py-1 text-right"
                      :class="line.shortage > 0 ? 'font-medium text-red-600' : 'text-dark'"
                    >
                      {{ line.shortage }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <!-- Reception Form Modal -->
      <div
        v-if="showReceptionForm"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
        data-testid="reception-form-modal"
      >
        <div class="w-full max-w-lg rounded-xl bg-white p-6 shadow-xl">
          <h3 class="mb-4 text-lg font-semibold text-dark">
            {{ isEditing ? 'Modifier la réception' : 'Réception' }} — {{ currentSupplier }}
          </h3>
          <table class="mb-4 w-full text-sm" data-testid="reception-form-table">
            <thead>
              <tr class="border-b border-gray-200 text-left">
                <th class="pb-1 font-medium text-dark">Produit</th>
                <th class="pb-1 text-right font-medium text-dark">Commandé</th>
                <th class="pb-1 text-right font-medium text-dark">Reçu</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="item in receptionForm"
                :key="item.productId"
                class="border-b border-gray-100"
              >
                <td class="py-2 text-dark">{{ item.productName }}</td>
                <td class="py-2 text-right text-dark">{{ item.orderedQuantity }}</td>
                <td class="py-2 text-right">
                  <input
                    v-model.number="item.receivedQuantity"
                    type="number"
                    min="0"
                    :max="item.orderedQuantity"
                    class="w-20 rounded border border-gray-300 px-2 py-1 text-right text-sm focus:border-primary focus:outline-none"
                    data-testid="reception-qty-input"
                  />
                </td>
              </tr>
            </tbody>
          </table>
          <div class="flex justify-end gap-3">
            <button
              class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-dark hover:bg-gray-50"
              data-testid="reception-form-cancel"
              @click="showReceptionForm = false"
            >
              Annuler
            </button>
            <button
              class="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-dark shadow-sm transition-colors hover:bg-primary/90 disabled:opacity-50"
              :disabled="submitting"
              data-testid="reception-form-submit"
              @click="submitReception"
            >
              {{ submitting ? 'Enregistrement...' : 'Valider la réception' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Step 2: Shortage preview (visible when all suppliers received) -->
      <div v-if="receptionStatus.allReceived" class="mb-8" data-testid="reception-shortages">
        <h2 class="mb-4 text-lg font-semibold text-dark">Aperçu des manques</h2>

        <EmptyState
          v-if="!hasShortages"
          message="Aucun manque détecté — toutes les quantités ont été reçues"
          data-testid="reception-no-shortages"
        />

        <template v-else>
          <div class="mb-4 rounded-lg border border-gray-200 bg-white overflow-hidden">
            <table class="w-full text-sm" data-testid="shortage-table">
              <thead>
                <tr class="border-b border-gray-200 bg-gray-50 text-left">
                  <th class="px-4 py-2 font-medium text-dark">Produit</th>
                  <th class="px-4 py-2 font-medium text-dark">Fournisseur</th>
                  <th class="px-4 py-2 text-right font-medium text-dark">Commandé</th>
                  <th class="px-4 py-2 text-right font-medium text-dark">Reçu</th>
                  <th class="px-4 py-2 text-right font-medium text-dark">Manque</th>
                  <th class="px-4 py-2 text-right font-medium text-dark">Commandes affectées</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="item in shortagePreview"
                  :key="item.productId"
                  class="border-b border-gray-100"
                  data-testid="shortage-row"
                >
                  <td class="px-4 py-2 text-dark">{{ item.productName }}</td>
                  <td class="px-4 py-2 text-brown">{{ item.supplier }}</td>
                  <td class="px-4 py-2 text-right text-dark">{{ item.orderedQty }}</td>
                  <td class="px-4 py-2 text-right text-dark">{{ item.receivedQty }}</td>
                  <td class="px-4 py-2 text-right font-medium text-red-600">{{ item.shortage }}</td>
                  <td class="px-4 py-2 text-right text-dark">{{ item.affectedOrderCount }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <button
            class="rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white shadow-sm transition-colors hover:bg-red-700 disabled:opacity-50"
            :disabled="submitting"
            data-testid="apply-adjustments-btn"
            @click="applyAdjustments"
          >
            {{ submitting ? 'Application...' : 'Appliquer les ajustements' }}
          </button>
        </template>
      </div>

      <!-- Step 3: Refund preview -->
      <div
        v-if="receptionStatus.allReceived && refundPreview.length > 0"
        class="mb-8"
        data-testid="reception-refunds"
      >
        <h2 class="mb-4 text-lg font-semibold text-dark">Remboursements</h2>

        <!-- Refund result summary -->
        <div
          v-if="refundResult"
          class="mb-4 rounded-lg border border-green-200 bg-green-50 p-4"
          data-testid="refund-result"
        >
          <p class="font-medium text-green-800">
            {{ refundResult.totalSucceeded }} remboursement(s) réussi(s) sur
            {{ refundResult.totalProcessed }} traité(s)
          </p>
          <p class="text-sm text-green-700">
            Montant total remboursé : {{ formatAmount(refundResult.totalAmountRefunded) }}
          </p>
          <p v-if="refundResult.totalFailed > 0" class="text-sm text-red-600">
            {{ refundResult.totalFailed }} remboursement(s) échoué(s)
          </p>
        </div>

        <div class="mb-4 rounded-lg border border-gray-200 bg-white overflow-hidden">
          <table class="w-full text-sm" data-testid="refund-table">
            <thead>
              <tr class="border-b border-gray-200 bg-gray-50 text-left">
                <th class="px-4 py-2 font-medium text-dark">Commande</th>
                <th class="px-4 py-2 font-medium text-dark">Client</th>
                <th class="px-4 py-2 text-right font-medium text-dark">Montant initial</th>
                <th class="px-4 py-2 text-right font-medium text-dark">Montant ajusté</th>
                <th class="px-4 py-2 text-right font-medium text-dark">Remboursement</th>
                <th class="px-4 py-2 font-medium text-dark">Statut</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="order in refundPreview"
                :key="order.orderId"
                class="border-b border-gray-100"
                data-testid="refund-row"
              >
                <td class="px-4 py-2 text-dark">{{ order.orderNumber }}</td>
                <td class="px-4 py-2 text-dark">{{ order.customerName }}</td>
                <td class="px-4 py-2 text-right text-dark">
                  {{ formatAmount(order.originalAmount) }}
                </td>
                <td class="px-4 py-2 text-right text-dark">
                  {{ formatAmount(order.adjustedAmount) }}
                </td>
                <td class="px-4 py-2 text-right font-medium text-red-600">
                  {{ formatAmount(order.refundAmount) }}
                </td>
                <td class="px-4 py-2">
                  <span
                    :class="[
                      'inline-block rounded-full px-2 py-0.5 text-xs font-medium',
                      order.refundStatus === 'SUCCEEDED'
                        ? 'bg-green-100 text-green-800'
                        : order.refundStatus === 'FAILED'
                          ? 'bg-red-100 text-red-800'
                          : 'bg-yellow-100 text-yellow-800',
                    ]"
                    data-testid="refund-status"
                  >
                    {{
                      order.refundStatus === 'SUCCEEDED'
                        ? 'Remboursé'
                        : order.refundStatus === 'FAILED'
                          ? 'Échoué'
                          : 'En attente'
                    }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <button
          class="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-dark shadow-sm transition-colors hover:bg-primary/90 disabled:opacity-50"
          :disabled="submitting"
          data-testid="process-refunds-btn"
          @click="processRefunds"
        >
          {{ submitting ? 'Traitement...' : 'Lancer les remboursements' }}
        </button>
      </div>
    </template>
  </div>
</template>
