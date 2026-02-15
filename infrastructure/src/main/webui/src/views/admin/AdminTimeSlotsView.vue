<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import Button from '@/components/ui/Button.vue'
import EmptyState from '@/components/ui/EmptyState.vue'
import Pagination from '@/components/ui/Pagination.vue'
import TimeSlotForm from '@/components/admin/TimeSlotForm.vue'
import { useVenteStore } from '@/stores/venteStore'
import { storeToRefs } from 'pinia'
import { useToast } from '@/composables/useToast'
import { useCursorPagination } from '@/composables/useCursorPagination'
import {
  useAdminTimeslotsQuery,
  useCreateTimeslotMutation,
  useUpdateTimeslotMutation,
  useDeleteTimeslotMutation,
} from '@/composables/api/useAdminTimeslotsApi'
import AdminTable from '@/components/admin/AdminTable.vue'
import type {
  TimeSlotResponse,
  CreateTimeSlotRequest,
  UpdateTimeSlotRequest,
} from '@/api/generated/model'

const toast = useToast()
const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)
const pagination = useCursorPagination()

watch(selectedVenteId, () => pagination.reset())

const { data: pageData, isLoading: loading } = useAdminTimeslotsQuery(
  selectedVenteId,
  pagination.currentCursor,
)
const timeSlots = computed(() => pageData.value?.data ?? [])
const pageInfo = computed(() => pageData.value?.pageInfo ?? { endCursor: null, hasNext: false })
const createMutation = useCreateTimeslotMutation(selectedVenteId)
const updateMutation = useUpdateTimeslotMutation(selectedVenteId)
const deleteMutationHook = useDeleteTimeslotMutation(selectedVenteId)

const showForm = ref(false)
const editingTimeSlot = ref<TimeSlotResponse | undefined>(undefined)
const submitting = ref(false)
const confirmingDeleteId = ref<number | null>(null)

function openCreateForm() {
  editingTimeSlot.value = undefined
  showForm.value = true
  confirmingDeleteId.value = null
}

function openEditForm(timeSlot: TimeSlotResponse) {
  editingTimeSlot.value = timeSlot
  showForm.value = true
  confirmingDeleteId.value = null
}

function closeForm() {
  showForm.value = false
  editingTimeSlot.value = undefined
}

async function onSubmit(data: {
  date: string
  startTime: string
  endTime: string
  capacity: number
}) {
  if (!selectedVenteId.value) return
  submitting.value = true
  try {
    if (editingTimeSlot.value) {
      const updateData: UpdateTimeSlotRequest = {
        date: data.date,
        startTime: data.startTime,
        endTime: data.endTime,
        capacity: data.capacity,
      }
      await updateMutation.mutateAsync({ id: editingTimeSlot.value.id, data: updateData })
      toast.success('Créneau mis à jour')
    } else {
      const createData: CreateTimeSlotRequest = {
        date: data.date,
        startTime: data.startTime,
        endTime: data.endTime,
        capacity: data.capacity,
      }
      await createMutation.mutateAsync(createData)
      toast.success('Créneau créé')
    }
    closeForm()
  } catch {
    toast.error('Erreur lors de la sauvegarde du créneau')
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

async function confirmDeleteAction(slot: TimeSlotResponse) {
  if (!selectedVenteId.value) return
  try {
    await deleteMutationHook.mutateAsync({ id: slot.id, force: slot.reserved > 0 })
    toast.success('Créneau supprimé')
    confirmingDeleteId.value = null
  } catch {
    toast.error('Erreur lors de la suppression du créneau')
  }
}

function formatDate(dateStr: string): string {
  const [year, month, day] = dateStr.split('-')
  return `${day}/${month}/${year}`
}

function formatTime(startTime: string, endTime: string): string {
  return `${startTime} — ${endTime}`
}
</script>

<template>
  <div>
    <div class="mb-6 flex items-center justify-between">
      <h1 class="text-2xl font-bold text-dark">Créneaux de retrait</h1>
      <Button variant="primary" data-testid="add-timeslot-btn" @click="openCreateForm">
        Ajouter un créneau
      </Button>
    </div>

    <TimeSlotForm
      v-if="showForm"
      :time-slot="editingTimeSlot"
      :loading="submitting"
      class="mb-6"
      @submit="onSubmit"
      @cancel="closeForm"
    />

    <div v-if="loading" class="py-12 text-center text-brown">Chargement des créneaux...</div>

    <EmptyState
      v-else-if="(!timeSlots || timeSlots.length === 0) && !showForm"
      message="Aucun créneau. Ajoutez votre premier créneau de retrait."
      action-label="Ajouter un créneau"
      @action="openCreateForm"
    />

    <AdminTable
      v-else-if="timeSlots && timeSlots.length > 0"
      :columns="['Date', 'Horaire', 'Capacité', 'Réservations', 'Actions']"
      caption="Liste des créneaux de retrait"
      dataTestid="timeslot-table"
    >
      <tr
        v-for="slot in timeSlots"
        :key="slot.id"
        class="border-t border-gray-100 transition-colors hover:bg-surface"
        data-testid="timeslot-row"
      >
        <td class="px-4 py-3 font-medium text-dark">{{ formatDate(slot.date) }}</td>
        <td class="px-4 py-3 text-dark">{{ formatTime(slot.startTime, slot.endTime) }}</td>
        <td class="px-4 py-3 text-dark">{{ slot.capacity }}</td>
        <td class="px-4 py-3">
          <span
            :class="[
              'inline-block rounded-full px-2 py-0.5 text-xs font-medium',
              slot.reserved > 0 ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600',
            ]"
          >
            {{ slot.reserved }} / {{ slot.capacity }}
          </span>
        </td>
        <td class="px-4 py-3">
          <div class="flex flex-col gap-2">
            <template v-if="confirmingDeleteId === slot.id">
              <p v-if="slot.reserved > 0" class="text-sm text-error">
                Ce créneau a {{ slot.reserved }} réservation(s). La suppression est irréversible.
              </p>
              <div class="flex gap-2">
                <Button
                  variant="danger"
                  size="md"
                  :aria-label="
                    'Confirmer la suppression du créneau de ' +
                    slot.startTime +
                    ' à ' +
                    slot.endTime
                  "
                  @click="confirmDeleteAction(slot)"
                >
                  Supprimer
                </Button>
                <Button variant="ghost" size="md" @click="cancelDelete"> Annuler </Button>
              </div>
            </template>
            <template v-else>
              <div class="flex gap-2">
                <Button
                  variant="secondary"
                  size="md"
                  :aria-label="'Modifier le créneau de ' + slot.startTime + ' à ' + slot.endTime"
                  @click="openEditForm(slot)"
                >
                  Modifier
                </Button>
                <Button
                  variant="danger"
                  size="md"
                  :aria-label="'Supprimer le créneau de ' + slot.startTime + ' à ' + slot.endTime"
                  @click="startDelete(slot.id)"
                >
                  Supprimer
                </Button>
              </div>
            </template>
          </div>
        </td>
      </tr>
    </AdminTable>

    <Pagination
      v-if="timeSlots && timeSlots.length > 0"
      :has-next="pageInfo.hasNext"
      :has-previous="pagination.hasPrevious.value"
      :page-number="pagination.pageNumber.value"
      :loading="loading"
      @next="pagination.goToNext(pageInfo.endCursor!)"
      @previous="pagination.goToPrevious()"
    />
  </div>
</template>
