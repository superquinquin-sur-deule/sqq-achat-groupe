<script setup lang="ts">
import { ref, watch } from 'vue'
import AdminSidenav from '@/components/admin/AdminSidenav.vue'
import { useVenteStore } from '@/stores/venteStore'
import { useAdminVentesQuery } from '@/composables/api/useAdminVentesApi'

const sidenavOpen = ref(false)
const venteStore = useVenteStore()
const { data: ventes, isLoading: loading } = useAdminVentesQuery()

watch(ventes, (data) => {
  if (!data) return
  const first = data[0]
  if (first && venteStore.selectedVenteId === null) {
    venteStore.selectedVenteId = first.id
  }
  if (
    venteStore.selectedVenteId !== null &&
    !data.find((v) => v.id === venteStore.selectedVenteId)
  ) {
    venteStore.selectedVenteId = first?.id ?? null
  }
})
</script>

<template>
  <div class="min-h-screen bg-surface">
    <AdminSidenav v-model="sidenavOpen" />

    <!-- Mobile top bar -->
    <div class="sticky top-0 z-30 flex h-14 items-center gap-3 bg-dark px-4 md:hidden print:hidden">
      <button
        type="button"
        class="text-white hover:text-white/80"
        aria-label="Ouvrir le menu"
        @click="sidenavOpen = true"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
          fill="currentColor"
          class="h-6 w-6"
          aria-hidden="true"
        >
          <path
            fill-rule="evenodd"
            d="M3 6.75A.75.75 0 0 1 3.75 6h16.5a.75.75 0 0 1 0 1.5H3.75A.75.75 0 0 1 3 6.75ZM3 12a.75.75 0 0 1 .75-.75h16.5a.75.75 0 0 1 0 1.5H3.75A.75.75 0 0 1 3 12Zm0 5.25a.75.75 0 0 1 .75-.75h16.5a.75.75 0 0 1 0 1.5H3.75a.75.75 0 0 1-.75-.75Z"
            clip-rule="evenodd"
          />
        </svg>
      </button>
      <span class="text-lg font-bold text-white">Administration</span>
    </div>

    <!-- Main content -->
    <main class="mx-auto max-w-7xl px-4 py-6 md:pl-64 print:pl-0 md:px-6">
      <div v-if="loading" class="py-12 text-center text-brown">Chargement...</div>
      <RouterView v-else />
    </main>
  </div>
</template>
