<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { useVenteStore } from '@/stores/venteStore'
import { storeToRefs } from 'pinia'
import { useToast } from '@/composables/useToast'
import { customFetch } from '@/api/mutator/custom-fetch'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)

const supplier = route.params.supplier as string

interface ReceptionLine {
  productId: number
  productName: string
  orderedQuantity: number
  receivedQuantity: number
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

const loading = ref(true)
const isEditing = ref(false)
const submitting = ref(false)
const receptionForm = ref<
  { productId: number; productName: string; orderedQuantity: number; receivedQuantity: number }[]
>([])

const title = computed(() =>
  isEditing.value ? `Modifier la réception — ${supplier}` : `Réception — ${supplier}`,
)

async function fetchSupplierData() {
  if (!selectedVenteId.value) return
  loading.value = true
  try {
    const res: any = await customFetch(
      `/api/admin/ventes/${selectedVenteId.value}/receptions`,
      { method: 'GET' },
    )
    const data: ReceptionStatus = res.data.data
    const supplierData = data.suppliers.find((s) => s.supplier === supplier)
    if (!supplierData) {
      toast.error('Fournisseur introuvable')
      router.push({ name: 'admin-reception' })
      return
    }
    isEditing.value = supplierData.status === 'COMPLETED'
    receptionForm.value = supplierData.lines.map((l) => ({
      productId: l.productId,
      productName: l.productName,
      orderedQuantity: l.orderedQuantity,
      receivedQuantity: isEditing.value ? l.receivedQuantity : l.orderedQuantity,
    }))
  } catch {
    toast.error('Erreur lors du chargement des données')
    router.push({ name: 'admin-reception' })
  } finally {
    loading.value = false
  }
}

async function submitReception() {
  if (!selectedVenteId.value || submitting.value) return
  submitting.value = true
  try {
    await customFetch(`/api/admin/ventes/${selectedVenteId.value}/receptions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        supplier,
        lines: receptionForm.value.map((l) => ({
          productId: l.productId,
          receivedQuantity: l.receivedQuantity,
        })),
      }),
    })
    toast.success(isEditing.value ? 'Réception modifiée' : 'Réception enregistrée')
    router.push({ name: 'admin-reception' })
  } catch {
    toast.error("Erreur lors de l'enregistrement de la réception")
  } finally {
    submitting.value = false
  }
}

onMounted(fetchSupplierData)
</script>

<template>
  <div data-testid="reception-form-page">
    <div class="mb-6">
      <RouterLink :to="{ name: 'admin-reception' }" class="text-sm text-brown hover:text-dark">
        &larr; Retour aux réceptions
      </RouterLink>
    </div>

    <h1 class="mb-6 text-2xl font-bold text-dark">{{ title }}</h1>

    <div v-if="loading" class="py-12 text-center text-brown">Chargement...</div>

    <template v-else>
      <div class="rounded-xl border border-gray-200 bg-white p-6">
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
          <RouterLink
            :to="{ name: 'admin-reception' }"
            class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-dark hover:bg-gray-50"
            data-testid="reception-form-cancel"
          >
            Annuler
          </RouterLink>
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
    </template>
  </div>
</template>
