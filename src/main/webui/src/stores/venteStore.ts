import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { fetchAdminVentes } from '@/api/admin'
import type { AdminVente } from '@/types/vente'

export const useVenteStore = defineStore('vente', () => {
  const ventes = ref<AdminVente[]>([])
  const selectedVenteId = ref<number | null>(null)
  const loading = ref(false)

  const selectedVente = computed(() =>
    ventes.value.find((v) => v.id === selectedVenteId.value) ?? null,
  )

  const hasVentes = computed(() => ventes.value.length > 0)

  async function loadVentes() {
    loading.value = true
    try {
      ventes.value = await fetchAdminVentes()
      // Auto-select the first vente (most recent, sorted DESC by id from backend)
      const first = ventes.value[0]
      if (first && selectedVenteId.value === null) {
        selectedVenteId.value = first.id
      }
      // If selected vente no longer exists, reset
      if (selectedVenteId.value !== null && !ventes.value.find((v) => v.id === selectedVenteId.value)) {
        selectedVenteId.value = first?.id ?? null
      }
    } finally {
      loading.value = false
    }
  }

  function selectVente(id: number) {
    selectedVenteId.value = id
  }

  return { ventes, selectedVenteId, loading, selectedVente, hasVentes, loadVentes, selectVente }
})
