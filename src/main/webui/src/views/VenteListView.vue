<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { fetchVentes } from '@/api/ventes'
import type { Vente } from '@/types/vente'

const router = useRouter()
const ventes = ref<Vente[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

onMounted(async () => {
  try {
    ventes.value = await fetchVentes()
  } catch {
    error.value = 'Impossible de charger les ventes'
  } finally {
    loading.value = false
  }
})

function goToVente(venteId: number) {
  router.push(`/ventes/${venteId}`)
}
</script>

<template>
  <section class="mx-auto max-w-4xl px-4 py-8">
    <h1 class="mb-6 text-2xl font-bold text-dark">Nos ventes en cours</h1>

    <div v-if="loading" class="text-center text-brown">Chargement...</div>

    <div v-else-if="error" class="text-center text-red-600">{{ error }}</div>

    <div v-else-if="ventes.length === 0" class="text-center text-brown">
      Aucune vente en cours pour le moment.
    </div>

    <div v-else class="grid gap-4 sm:grid-cols-2">
      <button
        v-for="vente in ventes"
        :key="vente.id"
        data-testid="vente-card"
        class="rounded-lg border border-gray-200 bg-white p-6 text-left shadow-sm transition hover:shadow-md"
        @click="goToVente(vente.id)"
      >
        <h2 class="text-lg font-semibold text-dark">{{ vente.name }}</h2>
        <p v-if="vente.description" class="mt-2 text-sm text-brown">{{ vente.description }}</p>
      </button>
    </div>
  </section>
</template>
