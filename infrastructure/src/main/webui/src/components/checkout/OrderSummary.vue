<script setup lang="ts">
import type { CartItem } from '@/types/cart'
import type { TimeSlotResponse } from '@/api/generated/model'
import Card from '@/components/ui/Card.vue'
import Button from '@/components/ui/Button.vue'

interface CustomerInfo {
  name: string
  email: string
  phone: string
}

defineProps<{
  customerInfo: CustomerInfo
  timeSlot: TimeSlotResponse
  items: CartItem[]
  total: number
  isSubmitting: boolean
}>()

const emit = defineEmits<{
  confirm: []
  back: []
}>()

function formatPrice(price: number): string {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(price)
}

function formatTime(time: string): string {
  return time.substring(0, 5).replace(':', 'h')
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr + 'T00:00:00')
  return date.toLocaleDateString('fr-FR', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}
</script>

<template>
  <div class="mx-auto flex w-full max-w-[640px] flex-col gap-6">
    <Card>
      <h3 class="mb-3 text-base font-semibold text-dark">Vos coordonnées</h3>
      <dl class="flex flex-col gap-1 text-sm text-dark">
        <div class="flex gap-2">
          <dt class="font-medium">Nom :</dt>
          <dd>{{ customerInfo.name }}</dd>
        </div>
        <div class="flex gap-2">
          <dt class="font-medium">Email :</dt>
          <dd>{{ customerInfo.email }}</dd>
        </div>
        <div class="flex gap-2">
          <dt class="font-medium">Téléphone :</dt>
          <dd>{{ customerInfo.phone }}</dd>
        </div>
      </dl>
    </Card>

    <Card>
      <h3 class="mb-3 text-base font-semibold text-dark">Votre créneau</h3>
      <div class="rounded-lg bg-primary px-4 py-3 text-sm font-semibold text-dark">
        {{ formatDate(timeSlot.date) }} · {{ formatTime(timeSlot.startTime) }} —
        {{ formatTime(timeSlot.endTime) }}
      </div>
    </Card>

    <Card>
      <h3 class="mb-3 text-base font-semibold text-dark">Votre commande</h3>
      <ul class="flex flex-col gap-2">
        <li
          v-for="item in items"
          :key="item.productId"
          class="flex items-center justify-between text-sm text-dark"
        >
          <span>{{ item.name }} × {{ item.quantity }}</span>
          <span class="font-medium">{{ formatPrice(item.price * item.quantity) }}</span>
        </li>
      </ul>
      <div class="mt-3 flex items-center justify-between border-t border-gray-200 pt-3">
        <span class="text-base font-bold text-dark">Total</span>
        <span class="text-base font-bold text-dark">{{ formatPrice(total) }}</span>
      </div>
    </Card>

    <div class="flex flex-col gap-2 sm:flex-row-reverse">
      <Button
        variant="primary"
        :loading="isSubmitting"
        :disabled="isSubmitting"
        :aria-busy="isSubmitting"
        class="w-full sm:w-auto sm:min-w-[200px]"
        @click="emit('confirm')"
      >
        {{ isSubmitting ? 'Redirection vers le paiement...' : 'Payer ma commande' }}
      </Button>
      <Button
        variant="secondary"
        :disabled="isSubmitting"
        class="w-full sm:w-auto"
        @click="emit('back')"
      >
        Modifier
      </Button>
    </div>
  </div>
</template>
