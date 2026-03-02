<script setup lang="ts">
import { version } from '../../../package.json'

const year = new Date().getFullYear()
</script>

<template>
  <footer class="py-4 text-center text-sm text-brown/60">
    &copy; Superquinquin {{ year }} &mdash; v{{ version }}
  </footer>
</template>
