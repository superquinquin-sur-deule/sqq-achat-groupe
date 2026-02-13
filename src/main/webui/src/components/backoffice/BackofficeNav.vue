<script setup lang="ts">
import { RouterLink } from 'vue-router'

const navItems = [
  { to: '/backoffice/orders', label: 'Commandes', testid: 'backoffice-nav-orders' },
  { to: '/backoffice/supplier-order', label: 'Bon fournisseur', testid: 'backoffice-nav-supplier' },
  { to: '/backoffice/preparation', label: 'Préparation', testid: 'backoffice-nav-preparation' },
  { to: '/backoffice/distribution', label: 'Distribution', testid: 'backoffice-nav-distribution' },
]
</script>

<template>
  <nav aria-label="Navigation back-office" data-testid="backoffice-nav">
    <ul class="flex gap-6">
      <li v-for="item in navItems" :key="item.to">
        <RouterLink
          :to="item.to"
          class="inline-block border-b-2 border-transparent px-1 py-2 text-sm font-medium text-white/60 transition-colors hover:text-white focus:outline-2 focus:outline-offset-2 focus:outline-primary"
          active-class="!border-primary !text-white"
          :aria-current="$route.path === item.to ? 'page' : undefined"
          :data-testid="item.testid"
        >
          {{ item.label }}
        </RouterLink>
      </li>
    </ul>
  </nav>
</template>
