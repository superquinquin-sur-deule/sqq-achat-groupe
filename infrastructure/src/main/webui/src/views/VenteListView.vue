<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useVentesQuery } from '@/composables/api/useVentesApi'

const router = useRouter()
const { data: ventes, isLoading: loading, isError } = useVentesQuery()

watch(ventes, (v) => {
  if (v?.length === 1) {
    router.replace({ name: 'home', params: { venteId: v[0]!.id } })
  }
})

const now = ref(Date.now())

let timer: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  timer = setInterval(() => {
    now.value = Date.now()
  }, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

function countdown(endDate: string | null): string | null {
  if (!endDate) return null
  const diff = new Date(endDate).getTime() - now.value
  if (diff <= 0) return 'Terminé'
  const days = Math.floor(diff / 86_400_000)
  const hours = Math.floor((diff % 86_400_000) / 3_600_000)
  const minutes = Math.floor((diff % 3_600_000) / 60_000)
  const parts: string[] = []
  if (days > 0) parts.push(`${days}j`)
  if (hours > 0) parts.push(`${hours}h`)
  parts.push(`${minutes}min`)
  return parts.join(' ')
}

function goToVente(venteId: number) {
  router.push({ name: 'home', params: { venteId } })
}
</script>

<template>
  <section class="mx-auto max-w-4xl px-4 py-8">
    <h1 class="mb-6 text-2xl font-bold text-dark">Nos ventes en cours</h1>

    <div v-if="loading" class="text-center text-brown">Chargement...</div>

    <div v-else-if="isError" class="text-center text-red-600">Impossible de charger les ventes</div>

    <div v-else-if="!ventes || ventes.length === 0" class="text-center text-brown">
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
        <p
          v-if="vente.endDate"
          class="mt-2 text-sm font-medium"
          :class="countdown(vente.endDate) === 'Terminé' ? 'text-error' : 'text-dark'"
          data-testid="vente-countdown"
        >
          {{
            countdown(vente.endDate) === 'Terminé'
              ? 'Terminé'
              : `Fin dans ${countdown(vente.endDate)}`
          }}
        </p>
      </button>
    </div>
  </section>
</template>
