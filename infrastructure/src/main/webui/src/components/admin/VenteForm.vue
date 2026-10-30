<script setup lang="ts">
import { useForm, useField } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { z } from 'zod'
import { computed, nextTick, onMounted } from 'vue'
import Input from '@/components/ui/Input.vue'
import Button from '@/components/ui/Button.vue'
import type { AdminVenteResponse } from '@/api/generated/model'
import { PICKUP_MODE_DATE_RANGE, PICKUP_MODE_TIME_SLOTS } from '@/utils/pickup-formatters'

const props = defineProps<{
  vente?: AdminVenteResponse
  loading?: boolean
}>()

const emit = defineEmits<{
  submit: [
    data: {
      name: string
      description: string
      startDate: string
      endDate: string
      pickupMode: string
      pickupStartDate: string
      pickupEndDate: string
    },
  ]
  cancel: []
}>()

const isEdit = !!props.vente
// Le mode de retrait est figé dès qu'une commande existe : basculer rendrait
// incohérentes les commandes déjà rattachées à un créneau (le backend renvoie un 409).
const modeLocked = !!props.vente?.hasOrders

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
      pickupMode: z
        .enum([PICKUP_MODE_TIME_SLOTS, PICKUP_MODE_DATE_RANGE])
        .default(PICKUP_MODE_TIME_SLOTS),
      pickupStartDate: z.string().optional().default(''),
      pickupEndDate: z.string().optional().default(''),
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
    )
    .refine((data) => data.pickupMode !== PICKUP_MODE_DATE_RANGE || !!data.pickupStartDate, {
      message: 'La date de début de retrait est requise',
      path: ['pickupStartDate'],
    })
    .refine((data) => data.pickupMode !== PICKUP_MODE_DATE_RANGE || !!data.pickupEndDate, {
      message: 'La date de fin de retrait est requise',
      path: ['pickupEndDate'],
    })
    .refine(
      (data) => {
        if (data.pickupMode !== PICKUP_MODE_DATE_RANGE) return true
        if (!data.pickupStartDate || !data.pickupEndDate) return true
        return data.pickupEndDate >= data.pickupStartDate
      },
      {
        message: 'La date de fin de retrait doit être après la date de début',
        path: ['pickupEndDate'],
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
        pickupMode: (props.vente.pickupMode ?? PICKUP_MODE_TIME_SLOTS) as
          | typeof PICKUP_MODE_TIME_SLOTS
          | typeof PICKUP_MODE_DATE_RANGE,
        pickupStartDate: props.vente.pickupStartDate ?? '',
        pickupEndDate: props.vente.pickupEndDate ?? '',
      }
    : {
        name: '',
        description: '',
        startDate: '',
        endDate: '',
        pickupMode: PICKUP_MODE_TIME_SLOTS as typeof PICKUP_MODE_TIME_SLOTS,
        pickupStartDate: '',
        pickupEndDate: '',
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
const {
  value: endDate,
  errorMessage: endDateError,
  handleBlur: endDateBlur,
} = useField<string>('endDate')
const { value: pickupMode } = useField<string>('pickupMode')
const {
  value: pickupStartDate,
  errorMessage: pickupStartDateError,
  handleBlur: pickupStartDateBlur,
} = useField<string>('pickupStartDate')
const {
  value: pickupEndDate,
  errorMessage: pickupEndDateError,
  handleBlur: pickupEndDateBlur,
} = useField<string>('pickupEndDate')

const usesDateRange = computed(() => pickupMode.value === PICKUP_MODE_DATE_RANGE)

onMounted(async () => {
  await nextTick()
  const el = document.getElementById('vente-name')
  el?.focus()
})

const onSubmit = handleSubmit((values) => {
  const dateRange = values.pickupMode === PICKUP_MODE_DATE_RANGE
  emit('submit', {
    name: values.name,
    description: values.description ?? '',
    startDate: values.startDate ?? '',
    endDate: values.endDate ?? '',
    pickupMode: values.pickupMode,
    pickupStartDate: dateRange ? (values.pickupStartDate ?? '') : '',
    pickupEndDate: dateRange ? (values.pickupEndDate ?? '') : '',
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
          label="Date de début des commandes"
          type="datetime-local"
          data-testid="vente-start-date-input"
          :error="startDateError ?? ''"
          @blur="startDateBlur"
        />
        <Input
          id="vente-end-date"
          v-model="endDate"
          label="Date de fin des commandes"
          type="datetime-local"
          data-testid="vente-end-date-input"
          :error="endDateError ?? ''"
          @blur="endDateBlur"
        />
      </div>

      <div class="flex flex-col gap-1">
        <label for="vente-pickup-mode" class="text-sm font-medium text-dark">
          Mode de retrait
        </label>
        <select
          id="vente-pickup-mode"
          v-model="pickupMode"
          data-testid="vente-pickup-mode-select"
          :disabled="modeLocked"
          class="min-h-[44px] rounded-lg border border-gray-300 px-3 py-2 text-dark transition-colors focus:border-primary focus:outline-2 focus:outline-offset-2 focus:outline-dark disabled:cursor-not-allowed disabled:border-gray-200 disabled:bg-gray-100 disabled:opacity-50"
        >
          <option :value="PICKUP_MODE_TIME_SLOTS">Créneaux de retrait</option>
          <option :value="PICKUP_MODE_DATE_RANGE">Plage de dates (sans créneau)</option>
        </select>
        <p v-if="modeLocked" class="text-sm text-brown" data-testid="vente-pickup-mode-locked">
          Le mode de retrait ne peut plus être modifié : cette vente contient des commandes.
        </p>
      </div>

      <div v-if="usesDateRange" class="grid grid-cols-1 gap-4 md:grid-cols-2">
        <Input
          id="vente-pickup-start-date"
          v-model="pickupStartDate"
          label="Début du retrait"
          type="date"
          data-testid="vente-pickup-start-input"
          :error="pickupStartDateError ?? ''"
          required
          @blur="pickupStartDateBlur"
        />
        <Input
          id="vente-pickup-end-date"
          v-model="pickupEndDate"
          label="Fin du retrait"
          type="date"
          data-testid="vente-pickup-end-input"
          :error="pickupEndDateError ?? ''"
          required
          @blur="pickupEndDateBlur"
        />
      </div>

      <div class="mt-2 flex gap-2">
        <Button type="submit" variant="primary" :loading="loading" data-testid="vente-submit-btn">
          Enregistrer
        </Button>
        <Button variant="ghost" @click="emit('cancel')"> Annuler </Button>
      </div>
    </form>
  </div>
</template>
