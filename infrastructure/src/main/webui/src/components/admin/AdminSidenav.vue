<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { cva } from 'class-variance-authority'
import { useAuth } from '@/composables/useAuth'
import { useVenteStore } from '@/stores/venteStore'
import { storeToRefs } from 'pinia'
import { useAdminVentesQuery } from '@/composables/api/useAdminVentesApi'

defineProps<{ modelValue: boolean }>()
const emit = defineEmits<{ 'update:modelValue': [value: boolean] }>()

const route = useRoute()
const { user } = useAuth()
const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)
const { data: pageData } = useAdminVentesQuery()
const ventes = computed(() => pageData.value?.data ?? [])

const adminLinks = [
  { to: '/admin/dashboard', label: 'Dashboard', testid: 'sidenav-dashboard', icon: 'dashboard' },
  { to: '/admin/products', label: 'Produits', testid: 'sidenav-products', icon: 'products' },
  { to: '/admin/timeslots', label: 'Créneaux', testid: 'sidenav-timeslots', icon: 'timeslots' },
  { to: '/admin/orders', label: 'Commandes', testid: 'sidenav-orders', icon: 'orders' },
  {
    to: '/admin/supplier-order',
    label: 'Bon fournisseur',
    testid: 'sidenav-supplier',
    icon: 'supplier',
  },
  {
    to: '/admin/reception',
    label: 'Réception',
    testid: 'sidenav-reception',
    icon: 'reception',
  },
  {
    to: '/admin/preparation',
    label: 'Préparation',
    testid: 'sidenav-preparation',
    icon: 'preparation',
  },
  {
    to: '/admin/distribution',
    label: 'Distribution',
    testid: 'sidenav-distribution',
    icon: 'distribution',
  },
]

const ventesLinks = [
  { to: '/admin/ventes', label: 'Ventes', testid: 'sidenav-ventes', icon: 'ventes' },
]

const navLinkVariants = cva(
  'flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors',
  {
    variants: {
      active: {
        true: 'bg-primary text-dark',
        false: 'text-white/70 hover:bg-white/10 hover:text-white',
      },
    },
  },
)

const sidebarVariants = cva(
  'fixed inset-y-0 left-0 z-50 flex w-60 flex-col bg-dark print:hidden transition-transform duration-200 ease-in-out md:translate-x-0',
  {
    variants: {
      open: {
        true: 'translate-x-0',
        false: '-translate-x-full',
      },
    },
  },
)

function isActive(to: string) {
  return route.path === to || route.path.startsWith(to + '/')
}

function close() {
  emit('update:modelValue', false)
}

function onVenteChange(event: Event) {
  const target = event.target as HTMLSelectElement
  const id = Number(target.value)
  if (id) {
    venteStore.selectVente(id)
  }
}
</script>

<template>
  <!-- Mobile backdrop -->
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="modelValue" class="fixed inset-0 z-40 bg-black/50 md:hidden" @click="close" />
    </Transition>
  </Teleport>

  <!-- Sidebar -->
  <aside data-testid="admin-sidenav" :class="sidebarVariants({ open: modelValue })">
    <!-- Logo / titre -->
    <div class="flex h-16 items-center gap-3 px-6">
      <img src="/logo.svg" alt="SuperQuinquin" class="h-8 w-8" />
      <span class="text-lg font-bold text-white">SuperQuinquin</span>
    </div>

    <!-- Vente Selector -->
    <div v-if="ventes && ventes.length > 0" class="px-3 pb-3">
      <select
        data-testid="sidenav-vente-selector"
        :value="selectedVenteId"
        class="w-full rounded-lg border border-white/20 bg-white/10 px-3 py-2 text-sm text-white focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary"
        @change="onVenteChange"
      >
        <option v-for="v in ventes" :key="v.id" :value="v.id" class="bg-dark text-white">
          {{ v.name }}
        </option>
      </select>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 overflow-y-auto px-3 py-4" aria-label="Navigation administration">
      <!-- Section Administration -->
      <p class="mb-2 px-3 text-xs font-semibold uppercase tracking-wider text-white/40">
        Vente en cours
      </p>
      <ul class="mb-6 space-y-1">
        <li v-for="link in adminLinks" :key="link.to">
          <RouterLink
            :to="link.to"
            :data-testid="link.testid"
            :class="navLinkVariants({ active: isActive(link.to) })"
            :aria-current="isActive(link.to) ? 'page' : undefined"
            @click="close"
          >
            <!-- Dashboard -->
            <svg
              v-if="link.icon === 'dashboard'"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-5 w-5"
              aria-hidden="true"
            >
              <path
                d="M11.47 3.841a.75.75 0 0 1 1.06 0l8.69 8.69a.75.75 0 1 0 1.06-1.061l-8.689-8.69a2.25 2.25 0 0 0-3.182 0l-8.69 8.69a.75.75 0 1 0 1.061 1.06l8.69-8.689Z"
              />
              <path
                d="m12 5.432 8.159 8.159c.03.03.06.058.091.086v6.198c0 1.035-.84 1.875-1.875 1.875H15.75a.75.75 0 0 1-.75-.75v-4.5a.75.75 0 0 0-.75-.75h-4.5a.75.75 0 0 0-.75.75V21a.75.75 0 0 1-.75.75H5.625a1.875 1.875 0 0 1-1.875-1.875v-6.198a2.29 2.29 0 0 0 .091-.086L12 5.432Z"
              />
            </svg>
            <!-- Products -->
            <svg
              v-else-if="link.icon === 'products'"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-5 w-5"
              aria-hidden="true"
            >
              <path
                d="M3.375 3C2.339 3 1.5 3.84 1.5 4.875v.75c0 1.036.84 1.875 1.875 1.875h17.25c1.035 0 1.875-.84 1.875-1.875v-.75C22.5 3.839 21.66 3 20.625 3H3.375Z"
              />
              <path
                fill-rule="evenodd"
                d="m3.087 9 .54 9.176A3 3 0 0 0 6.62 21h10.757a3 3 0 0 0 2.995-2.824L20.913 9H3.087Zm6.163 3.75A.75.75 0 0 1 10 12h4a.75.75 0 0 1 0 1.5h-4a.75.75 0 0 1-.75-.75Z"
                clip-rule="evenodd"
              />
            </svg>
            <!-- Timeslots -->
            <svg
              v-else-if="link.icon === 'timeslots'"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-5 w-5"
              aria-hidden="true"
            >
              <path
                fill-rule="evenodd"
                d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25ZM12.75 6a.75.75 0 0 0-1.5 0v6c0 .414.336.75.75.75h4.5a.75.75 0 0 0 0-1.5h-3.75V6Z"
                clip-rule="evenodd"
              />
            </svg>
            <!-- Ventes -->
            <svg
              v-else-if="link.icon === 'ventes'"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-5 w-5"
              aria-hidden="true"
            >
              <path
                d="M2.25 2.25a.75.75 0 0 0 0 1.5h1.386c.17 0 .318.114.362.278l2.558 9.592a3.752 3.752 0 0 0-2.806 3.63c0 .414.336.75.75.75h15.75a.75.75 0 0 0 0-1.5H5.378A2.25 2.25 0 0 1 7.5 15h11.218a.75.75 0 0 0 .674-.421 60.358 60.358 0 0 0 2.96-7.228.75.75 0 0 0-.525-.965A60.864 60.864 0 0 0 5.68 4.509l-.232-.867A1.875 1.875 0 0 0 3.636 2.25H2.25ZM3.75 20.25a1.5 1.5 0 1 1 3 0 1.5 1.5 0 0 1-3 0ZM16.5 20.25a1.5 1.5 0 1 1 3 0 1.5 1.5 0 0 1-3 0Z"
              />
            </svg>
            <!-- Orders -->
            <svg
              v-if="link.icon === 'orders'"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-5 w-5"
              aria-hidden="true"
            >
              <path
                fill-rule="evenodd"
                d="M7.502 6h7.128A3.375 3.375 0 0 1 18 9.375v9.375a3 3 0 0 0 3-3V6.108c0-1.505-1.125-2.811-2.664-2.94a48.972 48.972 0 0 0-.673-.05A3 3 0 0 0 15 1.5h-1.5a3 3 0 0 0-2.663 1.618c-.225.015-.45.032-.673.05C8.662 3.295 7.554 4.542 7.502 6ZM13.5 3A1.5 1.5 0 0 0 12 4.5h4.5A1.5 1.5 0 0 0 15 3h-1.5Z"
                clip-rule="evenodd"
              />
              <path
                fill-rule="evenodd"
                d="M3 9.375C3 8.339 3.84 7.5 4.875 7.5h9.75c1.036 0 1.875.84 1.875 1.875v11.25c0 1.035-.84 1.875-1.875 1.875h-9.75A1.875 1.875 0 0 1 3 20.625V9.375ZM6 12a.75.75 0 0 1 .75-.75h.008a.75.75 0 0 1 .75.75v.008a.75.75 0 0 1-.75.75H6.75a.75.75 0 0 1-.75-.75V12Zm2.25 0a.75.75 0 0 1 .75-.75h3.75a.75.75 0 0 1 0 1.5H9a.75.75 0 0 1-.75-.75ZM6 15a.75.75 0 0 1 .75-.75h.008a.75.75 0 0 1 .75.75v.008a.75.75 0 0 1-.75.75H6.75a.75.75 0 0 1-.75-.75V15Zm2.25 0a.75.75 0 0 1 .75-.75h3.75a.75.75 0 0 1 0 1.5H9a.75.75 0 0 1-.75-.75ZM6 18a.75.75 0 0 1 .75-.75h.008a.75.75 0 0 1 .75.75v.008a.75.75 0 0 1-.75.75H6.75a.75.75 0 0 1-.75-.75V18Zm2.25 0a.75.75 0 0 1 .75-.75h3.75a.75.75 0 0 1 0 1.5H9a.75.75 0 0 1-.75-.75Z"
                clip-rule="evenodd"
              />
            </svg>
            <!-- Supplier -->
            <svg
              v-else-if="link.icon === 'supplier'"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-5 w-5"
              aria-hidden="true"
            >
              <path
                d="M3.375 4.5C2.339 4.5 1.5 5.34 1.5 6.375V13.5h12V6.375c0-1.036-.84-1.875-1.875-1.875h-8.25ZM13.5 15h-12v2.625C1.5 18.66 2.34 19.5 3.375 19.5H5.25a3 3 0 1 1 6 0h1.5a3 3 0 1 1 6 0h.375c1.035 0 1.875-.84 1.875-1.875V16.5h-3.375c-1.035 0-1.875-.84-1.875-1.875v-3.75c0-1.036.84-1.875 1.875-1.875H21l-2.143-3.572A1.875 1.875 0 0 0 17.247 4.5H15v9h-1.5ZM8.25 19.5a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3ZM15.75 19.5a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3Z"
              />
              <path d="M16.5 12.75h5.25v-3h-5.25v3Z" />
            </svg>
            <!-- Preparation -->
            <svg
              v-else-if="link.icon === 'preparation'"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-5 w-5"
              aria-hidden="true"
            >
              <path
                fill-rule="evenodd"
                d="M7.502 6h7.128A3.375 3.375 0 0 1 18 9.375v9.375a3 3 0 0 0 3-3V6.108c0-1.505-1.125-2.811-2.664-2.94a48.972 48.972 0 0 0-.673-.05A3 3 0 0 0 15 1.5h-1.5a3 3 0 0 0-2.663 1.618c-.225.015-.45.032-.673.05C8.662 3.295 7.554 4.542 7.502 6ZM13.5 3A1.5 1.5 0 0 0 12 4.5h4.5A1.5 1.5 0 0 0 15 3h-1.5Z"
                clip-rule="evenodd"
              />
              <path
                fill-rule="evenodd"
                d="M3 9.375C3 8.339 3.84 7.5 4.875 7.5h9.75c1.036 0 1.875.84 1.875 1.875v11.25c0 1.035-.84 1.875-1.875 1.875h-9.75A1.875 1.875 0 0 1 3 20.625V9.375Zm9.586 1.97a.75.75 0 0 1 .067 1.058l-4.5 5.25a.75.75 0 0 1-1.1.033l-2.25-2.25a.75.75 0 1 1 1.06-1.06l1.669 1.668 3.996-4.665a.75.75 0 0 1 1.058-.067Z"
                clip-rule="evenodd"
              />
            </svg>
            <!-- Distribution -->
            <svg
              v-else-if="link.icon === 'distribution'"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-5 w-5"
              aria-hidden="true"
            >
              <path
                d="M7.5 3.375c0-1.036.84-1.875 1.875-1.875h.375a3.75 3.75 0 0 1 3.75 3.75v1.875C13.5 8.161 14.34 9 15.375 9h1.875A3.75 3.75 0 0 1 21 12.75v3.375C21 17.16 20.16 18 19.125 18h-9.75A1.875 1.875 0 0 1 7.5 16.125V3.375Z"
              />
              <path
                d="M15 5.25a5.23 5.23 0 0 0-1.279-3.434 9.768 9.768 0 0 1 6.963 6.963A5.23 5.23 0 0 0 17.25 7.5h-1.875A.375.375 0 0 1 15 7.125V5.25ZM4.875 6H6v10.125A3.375 3.375 0 0 0 9.375 19.5H16.5v1.125c0 1.035-.84 1.875-1.875 1.875h-9.75A1.875 1.875 0 0 1 3 20.625V7.875C3 6.839 3.84 6 4.875 6Z"
              />
            </svg>
            <!-- Reception -->
            <svg
              v-else-if="link.icon === 'reception'"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-5 w-5"
              aria-hidden="true"
            >
              <path
                fill-rule="evenodd"
                d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25ZM12.75 9a.75.75 0 0 0-1.5 0v2.25H9a.75.75 0 0 0 0 1.5h2.25V15a.75.75 0 0 0 1.5 0v-2.25H15a.75.75 0 0 0 0-1.5h-2.25V9Z"
                clip-rule="evenodd"
              />
            </svg>
            {{ link.label }}
          </RouterLink>
        </li>
      </ul>
      <p class="mb-2 px-3 text-xs font-semibold uppercase tracking-wider text-white/40">
        Gestion des ventes
      </p>
      <ul class="mb-6 space-y-1">
        <li v-for="link in ventesLinks" :key="link.to">
          <RouterLink
            :to="link.to"
            :data-testid="link.testid"
            :class="navLinkVariants({ active: isActive(link.to) })"
            :aria-current="isActive(link.to) ? 'page' : undefined"
            @click="close"
          >
            <!-- Ventes -->
            <svg
              v-if="link.icon === 'ventes'"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-5 w-5"
              aria-hidden="true"
            >
              <path
                d="M2.25 2.25a.75.75 0 0 0 0 1.5h1.386c.17 0 .318.114.362.278l2.558 9.592a3.752 3.752 0 0 0-2.806 3.63c0 .414.336.75.75.75h15.75a.75.75 0 0 0 0-1.5H5.378A2.25 2.25 0 0 1 7.5 15h11.218a.75.75 0 0 0 .674-.421 60.358 60.358 0 0 0 2.96-7.228.75.75 0 0 0-.525-.965A60.864 60.864 0 0 0 5.68 4.509l-.232-.867A1.875 1.875 0 0 0 3.636 2.25H2.25ZM3.75 20.25a1.5 1.5 0 1 1 3 0 1.5 1.5 0 0 1-3 0ZM16.5 20.25a1.5 1.5 0 1 1 3 0 1.5 1.5 0 0 1-3 0Z"
              />
            </svg>
            {{ link.label }}
          </RouterLink>
        </li>
      </ul>
    </nav>

    <!-- User info -->
    <div v-if="user" class="border-t border-white/10 px-4 py-4" data-testid="sidenav-user-info">
      <div class="flex items-center gap-3">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
          fill="currentColor"
          class="h-8 w-8 text-white/60"
          aria-hidden="true"
        >
          <path
            fill-rule="evenodd"
            d="M18.685 19.097A9.723 9.723 0 0 0 21.75 12c0-5.385-4.365-9.75-9.75-9.75S2.25 6.615 2.25 12a9.723 9.723 0 0 0 3.065 7.097A9.716 9.716 0 0 0 12 21.75a9.716 9.716 0 0 0 6.685-2.653Zm-2.54-1.228a7.493 7.493 0 0 0-8.29 0 7.5 7.5 0 0 1 8.29 0ZM12 13.5a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z"
            clip-rule="evenodd"
          />
        </svg>
        <div class="min-w-0">
          <p class="truncate text-sm font-medium text-white">{{ user.name }}</p>
          <p class="truncate text-xs text-white/50">{{ user.email }}</p>
        </div>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
