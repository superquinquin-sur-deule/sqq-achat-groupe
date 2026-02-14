import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useVenteStore = defineStore('vente', () => {
  const selectedVenteId = ref<number | null>(null)

  function selectVente(id: number) {
    selectedVenteId.value = id
  }

  return { selectedVenteId, selectVente }
})
