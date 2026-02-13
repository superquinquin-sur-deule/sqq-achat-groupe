<script setup lang="ts">
import { useForm, useField } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { z } from 'zod'
import { nextTick, onMounted } from 'vue'
import Input from '@/components/ui/Input.vue'
import Button from '@/components/ui/Button.vue'
import type { AdminTimeSlot } from '@/types/timeSlot'

const props = defineProps<{
  timeSlot?: AdminTimeSlot
  loading?: boolean
}>()

const emit = defineEmits<{
  submit: [data: { date: string; startTime: string; endTime: string; capacity: number }]
  cancel: []
}>()

const isEdit = !!props.timeSlot

const schema = toTypedSchema(
  z
    .object({
      date: z.string().min(1, 'La date est requise'),
      startTime: z.string().min(1, "L'heure de début est requise"),
      endTime: z.string().min(1, "L'heure de fin est requise"),
      capacity: z
        .string()
        .min(1, 'La capacité est requise')
        .refine(
          (v) => !isNaN(Number(v)) && Number(v) >= 1 && Number.isInteger(Number(v)),
          "La capacité doit être d'au moins 1",
        ),
    })
    .refine((data) => data.endTime > data.startTime, {
      message: "L'heure de fin doit être après l'heure de début",
      path: ['endTime'],
    }),
)

const { handleSubmit } = useForm({
  validationSchema: schema,
  initialValues: props.timeSlot
    ? {
        date: props.timeSlot.date,
        startTime: props.timeSlot.startTime,
        endTime: props.timeSlot.endTime,
        capacity: String(props.timeSlot.capacity),
      }
    : {
        date: '',
        startTime: '',
        endTime: '',
        capacity: '',
      },
})

const { value: date, errorMessage: dateError, handleBlur: dateBlur } = useField<string>('date')
const {
  value: startTime,
  errorMessage: startTimeError,
  handleBlur: startTimeBlur,
} = useField<string>('startTime')
const { value: endTime, errorMessage: endTimeError, handleBlur: endTimeBlur } = useField<string>('endTime')
const {
  value: capacity,
  errorMessage: capacityError,
  handleBlur: capacityBlur,
} = useField<string>('capacity')

onMounted(async () => {
  await nextTick()
  const el = document.getElementById('timeslot-date')
  el?.focus()
})

const onSubmit = handleSubmit((values) => {
  emit('submit', {
    date: values.date,
    startTime: values.startTime,
    endTime: values.endTime,
    capacity: Number(values.capacity),
  })
})
</script>

<template>
  <div class="rounded-xl border border-gray-200 bg-white p-6" data-testid="timeslot-form">
    <h2 class="mb-4 text-lg font-bold text-dark">
      {{ isEdit ? 'Modifier le créneau' : 'Ajouter un créneau' }}
    </h2>
    <form class="flex flex-col gap-4" @submit.prevent="onSubmit">
      <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
        <Input
          id="timeslot-date"
          v-model="date"
          label="Date"
          type="date"
          data-testid="timeslot-date-input"
          :error="dateError ?? ''"
          required
          @blur="dateBlur"
        />
        <Input
          id="timeslot-capacity"
          v-model="capacity"
          label="Capacité maximale"
          type="number"
          min="1"
          step="1"
          data-testid="timeslot-capacity-input"
          :error="capacityError ?? ''"
          required
          @blur="capacityBlur"
        />
      </div>
      <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
        <Input
          id="timeslot-start"
          v-model="startTime"
          label="Heure de début"
          type="time"
          data-testid="timeslot-start-input"
          :error="startTimeError ?? ''"
          required
          @blur="startTimeBlur"
        />
        <Input
          id="timeslot-end"
          v-model="endTime"
          label="Heure de fin"
          type="time"
          data-testid="timeslot-end-input"
          :error="endTimeError ?? ''"
          required
          @blur="endTimeBlur"
        />
      </div>
      <div class="mt-2 flex gap-2">
        <Button type="submit" variant="primary" :loading="loading" data-testid="timeslot-submit-btn">
          Enregistrer
        </Button>
        <Button variant="ghost" @click="emit('cancel')">
          Annuler
        </Button>
      </div>
    </form>
  </div>
</template>
