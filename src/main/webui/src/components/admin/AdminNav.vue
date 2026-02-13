<script setup lang="ts">
import { RouterLink } from 'vue-router'

const navItems = [
  { to: '/admin/dashboard', label: 'Dashboard' },
  { to: '/admin/products', label: 'Produits' },
  { to: '/admin/timeslots', label: 'Créneaux' },
  { to: '/admin/campaign', label: 'Campagne' },
]
</script>

<template>
  <nav aria-label="Navigation administration" data-testid="admin-nav">
    <ul class="flex gap-6">
      <li v-for="item in navItems" :key="item.to">
        <RouterLink
          :to="item.to"
          class="inline-block border-b-2 border-transparent px-1 py-2 text-sm font-medium text-white/60 transition-colors hover:text-white focus:outline-2 focus:outline-offset-2 focus:outline-primary"
          active-class="!border-primary !text-white"
          :aria-current="$route.path === item.to ? 'page' : undefined"
        >
          {{ item.label }}
        </RouterLink>
      </li>
    </ul>
  </nav>
</template>
