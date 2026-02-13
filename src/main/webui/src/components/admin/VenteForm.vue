<script setup lang="ts">
import { useForm, useField } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { z } from 'zod'
import { nextTick, onMounted } from 'vue'
import Input from '@/components/ui/Input.vue'
import Button from '@/components/ui/Button.vue'
import type { AdminVente } from '@/types/vente'

const props = defineProps<{
  vente?: AdminVente
  loading?: boolean
}>()

const emit = defineEmits<{
  submit: [data: { name: string; description: string; startDate: string; endDate: string }]
  cancel: []
}>()

const isEdit = !!props.vente

function toLocalDatetime(isoStr: string | null): string {
  if (!isoStr) return ''
  const d = new Date(isoStr)
  const offset = d.getTimezoneOffset()
  const local = new Date(d.getTime() - offset * 60000)
  return local.toISOString().slice(0, 16)
}

const schema = toTypedSchema(
  z
    .object({
      name: z.string().min(1, 'Le nom est requis'),
      description: z.string().optional().default(''),
      startDate: z.string().optional().default(''),
      endDate: z.string().optional().default(''),
    })
    .refine(
      (data) => {
        if (data.startDate && data.endDate) {
          return data.endDate > data.startDate
        }
        return true
      },
      {
        message: 'La date de fin doit être après la date de début',
        path: ['endDate'],
      },
    ),
)

const { handleSubmit } = useForm({
  validationSchema: schema,
  initialValues: props.vente
    ? {
        name: props.vente.name,
        description: props.vente.description ?? '',
        startDate: toLocalDatetime(props.vente.startDate),
        endDate: toLocalDatetime(props.vente.endDate),
      }
    : {
        name: '',
        description: '',
        startDate: '',
        endDate: '',
      },
})

const { value: name, errorMessage: nameError, handleBlur: nameBlur } = useField<string>('name')
const {
  value: description,
  errorMessage: descriptionError,
  handleBlur: descriptionBlur,
} = useField<string>('description')
const {
  value: startDate,
  errorMessage: startDateError,
  handleBlur: startDateBlur,
} = useField<string>('startDate')
const { value: endDate, errorMessage: endDateError, handleBlur: endDateBlur } = useField<string>('endDate')

onMounted(async () => {
  await nextTick()
  const el = document.getElementById('vente-name')
  el?.focus()
})

const onSubmit = handleSubmit((values) => {
  emit('submit', {
    name: values.name,
    description: values.description ?? '',
    startDate: values.startDate ?? '',
    endDate: values.endDate ?? '',
  })
})
</script>

<template>
  <div class="rounded-xl border border-gray-200 bg-white p-6" data-testid="vente-form">
    <h2 class="mb-4 text-lg font-bold text-dark">
      {{ isEdit ? 'Modifier la vente' : 'Créer une vente' }}
    </h2>
    <form class="flex flex-col gap-4" @submit.prevent="onSubmit">
      <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
        <Input
          id="vente-name"
          v-model="name"
          label="Nom"
          type="text"
          data-testid="vente-name-input"
          :error="nameError ?? ''"
          required
          @blur="nameBlur"
        />
        <Input
          id="vente-description"
          v-model="description"
          label="Description"
          type="text"
          data-testid="vente-description-input"
          :error="descriptionError ?? ''"
          @blur="descriptionBlur"
        />
      </div>
      <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
        <Input
          id="vente-start-date"
          v-model="startDate"
          label="Date de début"
          type="datetime-local"
          data-testid="vente-start-date-input"
          :error="startDateError ?? ''"
          @blur="startDateBlur"
        />
        <Input
          id="vente-end-date"
          v-model="endDate"
          label="Date de fin"
          type="datetime-local"
          data-testid="vente-end-date-input"
          :error="endDateError ?? ''"
          @blur="endDateBlur"
        />
      </div>
      <div class="mt-2 flex gap-2">
        <Button type="submit" variant="primary" :loading="loading" data-testid="vente-submit-btn">
          Enregistrer
        </Button>
        <Button variant="ghost" @click="emit('cancel')">
          Annuler
        </Button>
      </div>
    </form>
  </div>
</template>
