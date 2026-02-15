<script setup lang="ts">
import { ref, computed } from 'vue'
import Button from '@/components/ui/Button.vue'
import Pagination from '@/components/ui/Pagination.vue'
import VenteForm from '@/components/admin/VenteForm.vue'
import { useToast } from '@/composables/useToast'
import { useCursorPagination } from '@/composables/useCursorPagination'
import {
  useAdminVentesQuery,
  useCreateVenteMutation,
  useUpdateVenteMutation,
  useDeleteVenteMutation,
  useActivateVenteMutation,
  useDeactivateVenteMutation,
} from '@/composables/api/useAdminVentesApi'
import AdminTable from '@/components/admin/AdminTable.vue'
import type { AdminVenteResponse } from '@/api/generated/model'

const toast = useToast()
const pagination = useCursorPagination()

const { data: pageData, isLoading: loading } = useAdminVentesQuery(pagination.currentCursor)
const ventes = computed(() => pageData.value?.data ?? [])
const pageInfo = computed(() => pageData.value?.pageInfo ?? { endCursor: null, hasNext: false })

const createMutation = useCreateVenteMutation()
const updateMutation = useUpdateVenteMutation()
const deleteMutationHook = useDeleteVenteMutation()
const activateMutation = useActivateVenteMutation()
const deactivateMutation = useDeactivateVenteMutation()

const showForm = ref(false)
const editingVente = ref<AdminVenteResponse | undefined>(undefined)
const submitting = ref(false)
const confirmingDeleteId = ref<number | null>(null)

function openCreateForm() {
  editingVente.value = undefined
  showForm.value = true
  confirmingDeleteId.value = null
}

function openEditForm(vente: AdminVenteResponse) {
  editingVente.value = vente
  showForm.value = true
  confirmingDeleteId.value = null
}

function closeForm() {
  showForm.value = false
  editingVente.value = undefined
}

function toInstantOrUndefined(datetimeLocal: string): string | undefined {
  if (!datetimeLocal) return undefined
  return new Date(datetimeLocal).toISOString()
}

async function onSubmit(data: {
  name: string
  description: string
  startDate: string
  endDate: string
}) {
  submitting.value = true
  try {
    const payload = {
      name: data.name,
      description: data.description,
      startDate: toInstantOrUndefined(data.startDate),
      endDate: toInstantOrUndefined(data.endDate),
    }
    if (editingVente.value) {
      await updateMutation.mutateAsync({ id: editingVente.value.id, data: payload })
      toast.success('Vente mise à jour')
    } else {
      await createMutation.mutateAsync(payload)
      toast.success('Vente créée')
    }
    closeForm()
  } catch {
    toast.error('Erreur lors de la sauvegarde de la vente')
  } finally {
    submitting.value = false
  }
}

function startDelete(id: number) {
  confirmingDeleteId.value = id
}

function cancelDelete() {
  confirmingDeleteId.value = null
}

async function confirmDelete(id: number) {
  try {
    await deleteMutationHook.mutateAsync(id)
    toast.success('Vente supprimée')
    confirmingDeleteId.value = null
  } catch {
    toast.error('Erreur lors de la suppression de la vente')
  }
}

async function toggleStatus(vente: AdminVenteResponse) {
  try {
    if (vente.status === 'ACTIVE') {
      await deactivateMutation.mutateAsync(vente.id)
      toast.success('Vente désactivée')
    } else {
      await activateMutation.mutateAsync(vente.id)
      toast.success('Vente activée')
    }
  } catch {
    toast.error('Erreur lors de la mise à jour du statut')
  }
}

function formatDate(isoStr: string | null): string {
  if (!isoStr) return '—'
  return new Date(isoStr).toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

<template>
  <div>
    <div class="mb-6 flex items-center justify-between">
      <h1 class="text-2xl font-bold text-dark">Ventes</h1>
      <Button variant="primary" data-testid="add-vente-btn" @click="openCreateForm">
        Créer une vente
      </Button>
    </div>

    <VenteForm
      v-if="showForm"
      :vente="editingVente"
      :loading="submitting"
      class="mb-6"
      @submit="onSubmit"
      @cancel="closeForm"
    />

    <div
      v-if="(!ventes || ventes.length === 0) && !showForm && !loading"
      class="rounded-xl border border-gray-200 bg-white p-12 text-center"
      data-testid="empty-state"
    >
      <p class="mb-4 text-brown">Aucune vente. Créez votre première vente.</p>
      <Button variant="primary" @click="openCreateForm"> Créer une vente </Button>
    </div>

    <AdminTable
      v-else-if="ventes && ventes.length > 0"
      :columns="['Nom', 'Début', 'Fin', 'Statut', 'Actions']"
      caption="Liste des ventes"
      dataTestid="vente-table"
    >
      <tr
        v-for="vente in ventes"
        :key="vente.id"
        class="border-t border-gray-100 transition-colors hover:bg-surface"
        data-testid="vente-row"
      >
        <td class="px-4 py-3 font-medium text-dark">{{ vente.name }}</td>
        <td class="px-4 py-3 text-dark">{{ formatDate(vente.startDate) }}</td>
        <td class="px-4 py-3 text-dark">{{ formatDate(vente.endDate) }}</td>
        <td class="px-4 py-3">
          <span
            :class="[
              'inline-block rounded-full px-2 py-0.5 text-xs font-medium',
              vente.status === 'ACTIVE'
                ? 'bg-green-100 text-green-700'
                : 'bg-gray-100 text-gray-600',
            ]"
            data-testid="vente-status-badge"
          >
            {{ vente.status === 'ACTIVE' ? 'Active' : 'Fermée' }}
          </span>
        </td>
        <td class="px-4 py-3">
          <div class="flex gap-2">
            <template v-if="confirmingDeleteId === vente.id">
              <Button
                variant="danger"
                size="md"
                :aria-label="'Confirmer la suppression de ' + vente.name"
                @click="confirmDelete(vente.id)"
              >
                Supprimer
              </Button>
              <Button variant="ghost" size="md" @click="cancelDelete"> Annuler </Button>
            </template>
            <template v-else>
              <Button
                variant="secondary"
                size="md"
                :aria-label="'Modifier ' + vente.name"
                @click="openEditForm(vente)"
              >
                Modifier
              </Button>
              <Button
                :variant="vente.status === 'ACTIVE' ? 'ghost' : 'primary'"
                size="md"
                :aria-label="(vente.status === 'ACTIVE' ? 'Désactiver ' : 'Activer ') + vente.name"
                data-testid="vente-toggle-btn"
                @click="toggleStatus(vente)"
              >
                {{ vente.status === 'ACTIVE' ? 'Désactiver' : 'Activer' }}
              </Button>
              <Button
                variant="danger"
                size="md"
                :aria-label="'Supprimer ' + vente.name"
                @click="startDelete(vente.id)"
              >
                Supprimer
              </Button>
            </template>
          </div>
        </td>
      </tr>
    </AdminTable>

    <Pagination
      v-if="ventes && ventes.length > 0"
      :has-next="pageInfo.hasNext"
      :has-previous="pagination.hasPrevious.value"
      :page-number="pagination.pageNumber.value"
      :loading="loading"
      @next="pagination.goToNext(pageInfo.endCursor!)"
      @previous="pagination.goToPrevious()"
    />
  </div>
</template>
