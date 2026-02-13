<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Button from '@/components/ui/Button.vue'
import TimeSlotForm from '@/components/admin/TimeSlotForm.vue'
import { fetchTimeSlots, createTimeSlot, updateTimeSlot, deleteTimeSlot } from '@/api/admin'
import { fetchVentes } from '@/api/ventes'
import { useToast } from '@/composables/useToast'
import type { AdminTimeSlot, CreateTimeSlotData, UpdateTimeSlotData } from '@/types/timeSlot'

const toast = useToast()

const timeSlots = ref<AdminTimeSlot[]>([])
const loading = ref(false)
const venteId = ref<number | null>(null)
const showForm = ref(false)
const editingTimeSlot = ref<AdminTimeSlot | undefined>(undefined)
const submitting = ref(false)
const confirmingDeleteId = ref<number | null>(null)
const forceDelete = ref(false)

async function loadData() {
  loading.value = true
  try {
    const ventes = await fetchVentes()
    const firstVente = ventes[0]
    if (firstVente) {
      venteId.value = firstVente.id
      timeSlots.value = await fetchTimeSlots(venteId.value)
    }
  } catch {
    toast.error('Erreur lors du chargement des créneaux')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function openCreateForm() {
  editingTimeSlot.value = undefined
  showForm.value = true
  confirmingDeleteId.value = null
  forceDelete.value = false
}

function openEditForm(timeSlot: AdminTimeSlot) {
  editingTimeSlot.value = timeSlot
  showForm.value = true
  confirmingDeleteId.value = null
  forceDelete.value = false
}

function closeForm() {
  showForm.value = false
  editingTimeSlot.value = undefined
}

async function onSubmit(data: { date: string; startTime: string; endTime: string; capacity: number }) {
  if (!venteId.value) return
  submitting.value = true
  try {
    if (editingTimeSlot.value) {
      const updateData: UpdateTimeSlotData = {
        date: data.date,
        startTime: data.startTime,
        endTime: data.endTime,
        capacity: data.capacity,
      }
      await updateTimeSlot(editingTimeSlot.value.id, updateData)
      toast.success('Créneau mis à jour')
    } else {
      const createData: CreateTimeSlotData = {
        venteId: venteId.value,
        date: data.date,
        startTime: data.startTime,
        endTime: data.endTime,
        capacity: data.capacity,
      }
      await createTimeSlot(createData)
      toast.success('Créneau créé')
    }
    closeForm()
    timeSlots.value = await fetchTimeSlots(venteId.value)
  } catch {
    toast.error('Erreur lors de la sauvegarde du créneau')
  } finally {
    submitting.value = false
  }
}

function startDelete(id: number) {
  confirmingDeleteId.value = id
  forceDelete.value = false
}

function cancelDelete() {
  confirmingDeleteId.value = null
  forceDelete.value = false
}

async function confirmDeleteAction(slot: AdminTimeSlot) {
  if (!venteId.value) return
  try {
    await deleteTimeSlot(slot.id, slot.reserved > 0 ? true : undefined)
    toast.success('Créneau supprimé')
    confirmingDeleteId.value = null
    forceDelete.value = false
    timeSlots.value = await fetchTimeSlots(venteId.value)
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

    <div v-if="loading" class="py-12 text-center text-brown">
      Chargement des créneaux...
    </div>

    <div
      v-else-if="timeSlots.length === 0 && !showForm"
      class="rounded-xl border border-gray-200 bg-white p-12 text-center"
      data-testid="empty-state"
    >
      <p class="mb-4 text-brown">Aucun créneau. Ajoutez votre premier créneau de retrait.</p>
      <Button variant="primary" @click="openCreateForm">
        Ajouter un créneau
      </Button>
    </div>

    <div v-else-if="timeSlots.length > 0" class="overflow-x-auto rounded-xl border border-gray-200 bg-white">
      <table class="w-full" data-testid="timeslot-table">
        <caption class="sr-only">Liste des créneaux de retrait</caption>
        <thead>
          <tr class="bg-dark text-left text-sm text-white">
            <th class="px-4 py-3 font-medium">Date</th>
            <th class="px-4 py-3 font-medium">Horaire</th>
            <th class="px-4 py-3 font-medium">Capacité</th>
            <th class="px-4 py-3 font-medium">Réservations</th>
            <th class="px-4 py-3 font-medium">Actions</th>
          </tr>
        </thead>
        <tbody>
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
                  slot.reserved > 0
                    ? 'bg-green-100 text-green-700'
                    : 'bg-gray-100 text-gray-600',
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
                      :aria-label="'Confirmer la suppression du créneau de ' + slot.startTime + ' à ' + slot.endTime"
                      @click="confirmDeleteAction(slot)"
                    >
                      Supprimer
                    </Button>
                    <Button
                      variant="ghost"
                      size="md"
                      @click="cancelDelete"
                    >
                      Annuler
                    </Button>
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
        </tbody>
      </table>
    </div>
  </div>
</template>
