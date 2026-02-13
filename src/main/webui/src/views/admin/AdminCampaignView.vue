<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import Card from '@/components/ui/Card.vue'
import ToggleSwitch from '@/components/ui/ToggleSwitch.vue'
import { fetchCampaignVentes, fetchCampaignStatus, updateCampaignStatus } from '@/api/admin'
import { useToast } from '@/composables/useToast'
import type { CampaignStatus } from '@/types/campaign'

const toast = useToast()

const campaign = ref<CampaignStatus | null>(null)
const loading = ref(false)
const updating = ref(false)
const venteId = ref<number | null>(null)

const isActive = computed(() => campaign.value?.status === 'ACTIVE')

async function loadData() {
  loading.value = true
  try {
    const ventes = await fetchCampaignVentes()
    const firstVente = ventes[0]
    if (firstVente) {
      venteId.value = firstVente.id
      campaign.value = await fetchCampaignStatus(venteId.value)
    }
  } catch {
    toast.error('Erreur lors du chargement de la campagne')
  } finally {
    loading.value = false
  }
}

async function handleToggle() {
  if (!venteId.value || updating.value) return
  updating.value = true
  try {
    const newActive = !isActive.value
    campaign.value = await updateCampaignStatus(venteId.value, newActive)
    if (newActive) {
      toast.success('Période de commande activée')
    } else {
      toast.success('Période de commande désactivée')
    }
  } catch {
    toast.error('Erreur lors de la mise à jour de la campagne')
  } finally {
    updating.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <div>
    <h1 class="mb-6 text-2xl font-bold text-dark">Campagne</h1>

    <div v-if="loading" class="py-12 text-center text-brown">
      Chargement...
    </div>

    <div v-else-if="!venteId" class="py-12 text-center text-brown">
      Aucune vente configurée. Créez une vente pour activer la campagne.
    </div>

    <Card v-else data-testid="campaign-card">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-lg font-semibold text-dark">Période de commande</h2>
          <p data-testid="campaign-status-message" class="mt-1 text-sm text-brown">
            <template v-if="isActive">
              Les commandes sont ouvertes
            </template>
            <template v-else>
              Les commandes ne sont pas encore ouvertes
            </template>
          </p>
        </div>
        <ToggleSwitch
          :model-value="isActive"
          label="Période de commande"
          :disabled="updating"
          data-testid="campaign-toggle"
          @update:model-value="handleToggle"
        />
      </div>
    </Card>
  </div>
</template>
