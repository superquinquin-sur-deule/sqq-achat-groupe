<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { storeToRefs } from 'pinia'
import {
  useAdminOrderDetailQuery,
  useCancelOrderMutation,
} from '@/composables/api/useAdminOrdersApi'
import { useVenteStore } from '@/stores/venteStore'
import { useToast } from '@/composables/useToast'
import { formatPrice, formatDateTime, statusLabel, statusClasses } from '@/utils/order-formatters'
import { useSelectedVente } from '@/composables/useSelectedVente'
import { formatPickupRangeShort } from '@/utils/pickup-formatters'
import Button from '@/components/ui/Button.vue'

const route = useRoute()
const orderId = route.params.id as string
const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)
const toast = useToast()

const { data: order, isLoading: loading } = useAdminOrderDetailQuery(selectedVenteId, orderId)

const { selectedVente, venteUsesTimeSlots } = useSelectedVente()

const cancelMutation = useCancelOrderMutation(selectedVenteId)
const showCancelForm = ref(false)
const cancelReason = ref('')
const cancelError = ref<string | null>(null)

function openCancelForm() {
  cancelReason.value = ''
  cancelError.value = null
  showCancelForm.value = true
}

function closeCancelForm() {
  showCancelForm.value = false
}

async function confirmCancel() {
  const reason = cancelReason.value.trim()
  if (!reason) {
    cancelError.value = "La raison de l'annulation est requise"
    return
  }
  cancelError.value = null
  try {
    await cancelMutation.mutateAsync({ orderId, reason })
    showCancelForm.value = false
    toast.success('Commande annulée — stock et créneau libérés')
  } catch {
    cancelError.value = "Erreur lors de l'annulation de la commande"
    toast.error("Erreur lors de l'annulation de la commande")
  }
}

function formatSlot(slot: { date: string; startTime: string; endTime: string } | null): string {
  if (slot) return `${slot.date} ${slot.startTime}-${slot.endTime}`
  // Vente en plage de dates : l'absence de créneau est normale.
  if (!venteUsesTimeSlots.value) {
    return formatPickupRangeShort(
      selectedVente.value?.pickupStartDate,
      selectedVente.value?.pickupEndDate,
    )
  }
  return 'Créneau inconnu'
}
</script>

<template>
  <div>
    <div class="mb-6">
      <RouterLink :to="{ name: 'admin-orders' }" class="text-sm text-brown hover:text-dark">
        &larr; Retour aux commandes
      </RouterLink>
    </div>

    <div v-if="loading" class="py-12 text-center text-brown">Chargement de la commande...</div>

    <div v-else-if="order" class="space-y-6">
      <div class="flex flex-wrap items-center gap-4">
        <h1 class="text-2xl font-bold text-dark">{{ order.orderNumber }}</h1>
        <span
          :class="[
            'inline-block rounded-full px-3 py-1 text-sm font-medium',
            statusClasses(order.status),
          ]"
        >
          {{ statusLabel(order.status) }}
        </span>
        <Button
          v-if="order.status === 'CONFIRMED' && !showCancelForm"
          variant="danger"
          class="ml-auto"
          data-testid="order-cancel-btn"
          @click="openCancelForm"
        >
          Annuler la commande
        </Button>
      </div>
      <p class="text-sm text-brown">Créée le {{ formatDateTime(order.createdAt) }}</p>

      <!-- Motif d'annulation -->
      <div
        v-if="order.status === 'CANCELLED' && order.cancellationReason"
        class="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-800"
        data-testid="order-cancellation-reason"
      >
        <span class="font-semibold">Motif de l'annulation :</span>
        {{ order.cancellationReason }}
      </div>

      <!-- Annulation -->
      <div
        v-if="showCancelForm"
        class="rounded-xl border border-red-200 bg-red-50 p-6"
        data-testid="order-cancel-form"
      >
        <h2 class="text-lg font-semibold text-dark">Annuler la commande</h2>
        <p class="mt-1 text-sm text-brown">
          Le stock des produits sera restitué et la place du créneau libérée. Cette action est
          irréversible.
        </p>
        <label for="cancel-reason" class="mt-4 block text-sm font-medium text-dark">
          Raison de l'annulation
        </label>
        <textarea
          id="cancel-reason"
          v-model="cancelReason"
          rows="3"
          class="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary"
          placeholder="Ex : demande du client"
          data-testid="order-cancel-reason-input"
        ></textarea>
        <p v-if="cancelError" class="mt-2 text-sm text-red-700" data-testid="order-cancel-error">
          {{ cancelError }}
        </p>
        <div class="mt-4 flex gap-3">
          <Button
            variant="danger"
            :loading="cancelMutation.isPending.value"
            data-testid="order-cancel-confirm-btn"
            @click="confirmCancel"
          >
            Confirmer l'annulation
          </Button>
          <Button variant="secondary" data-testid="order-cancel-abort-btn" @click="closeCancelForm">
            Retour
          </Button>
        </div>
      </div>

      <!-- Coordonnées -->
      <div
        class="rounded-xl border border-gray-200 bg-white p-6"
        data-testid="order-detail-customer"
      >
        <h2 class="mb-4 text-lg font-semibold text-dark">Coordonnées</h2>
        <dl class="grid grid-cols-1 gap-3 sm:grid-cols-4">
          <div>
            <dt class="text-sm text-brown">Nom</dt>
            <dd class="font-medium text-dark">
              {{ order.customerLastName }} {{ order.customerFirstName }}
            </dd>
          </div>
          <div>
            <dt class="text-sm text-brown">Email</dt>
            <dd class="font-medium text-dark">{{ order.customerEmail }}</dd>
          </div>
          <div>
            <dt class="text-sm text-brown">Téléphone</dt>
            <dd class="font-medium text-dark">{{ order.customerPhone }}</dd>
          </div>
          <div>
            <dt class="text-sm text-brown">N° de coopérateur</dt>
            <dd class="font-medium text-dark" data-testid="order-detail-cooperator-number">
              {{ order.cooperatorNumber }}
            </dd>
          </div>
        </dl>
      </div>

      <!-- Créneau -->
      <div
        class="rounded-xl border border-gray-200 bg-white p-6"
        data-testid="order-detail-timeslot"
      >
        <h2 class="mb-4 text-lg font-semibold text-dark">
          {{ venteUsesTimeSlots ? 'Créneau de retrait' : 'Période de retrait' }}
        </h2>
        <p class="font-medium text-dark">{{ formatSlot(order.timeSlot) }}</p>
      </div>

      <!-- Produits -->
      <div
        class="overflow-x-auto rounded-xl border border-gray-200 bg-white"
        data-testid="order-detail-items"
      >
        <h2 class="px-6 pt-6 text-lg font-semibold text-dark">Produits commandés</h2>
        <table class="mt-4 w-full">
          <caption class="sr-only">
            Produits de la commande
          </caption>
          <thead>
            <tr class="bg-dark text-left text-sm text-white">
              <th class="px-6 py-3 font-medium">Produit</th>
              <th class="px-6 py-3 font-medium">Quantité</th>
              <th class="px-6 py-3 font-medium">Prix unitaire</th>
              <th class="px-6 py-3 font-medium">Sous-total</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in order.items" :key="index" class="border-t border-gray-100">
              <td class="px-6 py-3 font-medium text-dark">{{ item.productName }}</td>
              <td class="px-6 py-3 text-dark">{{ item.quantity }}</td>
              <td class="px-6 py-3 text-dark">{{ formatPrice(item.unitPrice) }}</td>
              <td class="px-6 py-3 text-dark">{{ formatPrice(item.subtotal) }}</td>
            </tr>
            <tr v-if="order.items.length === 0">
              <td colspan="4" class="px-6 py-6 text-center text-brown">
                Aucun produit dans cette commande
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Total -->
      <div class="rounded-xl border border-gray-200 bg-white p-6" data-testid="order-detail-total">
        <div class="flex items-center justify-between">
          <span class="text-lg font-semibold text-dark">Total</span>
          <span class="text-2xl font-bold text-dark">{{ formatPrice(order.totalAmount) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
