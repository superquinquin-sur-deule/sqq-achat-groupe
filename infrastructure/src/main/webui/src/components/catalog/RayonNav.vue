<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { toRayonId, type RayonColor } from '@/utils/rayon-colors'

const props = defineProps<{
  rayons: string[]
  colors: RayonColor[]
}>()

const activeRayon = ref(props.rayons[0] ?? '')

let observer: IntersectionObserver | null = null

onMounted(() => {
  observer = new IntersectionObserver(
    (entries) => {
      for (const entry of entries) {
        if (entry.isIntersecting) {
          const id = entry.target.id
          const rayon = props.rayons.find((r) => toRayonId(r) === id)
          if (rayon) activeRayon.value = rayon
        }
      }
    },
    { rootMargin: '-80px 0px -60% 0px', threshold: 0 },
  )

  for (const rayon of props.rayons) {
    const el = document.getElementById(toRayonId(rayon))
    if (el) observer.observe(el)
  }
})

onUnmounted(() => {
  observer?.disconnect()
})

function scrollTo(rayon: string) {
  const el = document.getElementById(toRayonId(rayon))
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}
</script>

<template>
  <div
    data-testid="rayon-nav"
    class="sticky top-0 z-10 border-b border-gray-200 bg-white/95 backdrop-blur-sm"
  >
    <div class="mx-auto max-w-5xl px-4 md:px-6 lg:px-8">
      <nav
        class="flex gap-2 overflow-x-auto py-3 [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]"
      >
        <button
          v-for="rayon in rayons"
          :key="rayon"
          class="shrink-0 rounded-full px-4 py-1.5 text-sm font-medium transition-colors"
          :class="
            activeRayon === rayon
              ? 'bg-primary text-dark'
              : 'bg-gray-100 text-brown hover:bg-gray-200'
          "
          @click="scrollTo(rayon)"
        >
          {{ rayon }}
        </button>
      </nav>
    </div>
  </div>
</template>
