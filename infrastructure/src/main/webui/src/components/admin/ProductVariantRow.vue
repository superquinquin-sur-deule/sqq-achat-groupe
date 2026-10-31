<script setup lang="ts">
import { computed } from 'vue'
import { useField } from 'vee-validate'
import Input from '@/components/ui/Input.vue'
import Button from '@/components/ui/Button.vue'

const props = defineProps<{
  index: number
  locked?: boolean
  removable?: boolean
  tauxTva: string
}>()

defineEmits<{
  remove: []
}>()

/**
 * Chaque champ est relié à son chemin dans le tableau `variants` du formulaire :
 * VeeValidate reste la source de vérité et affiche les erreurs par champ.
 */
const path = (field: string) => `variants[${props.index}].${field}`

const {
  value: label,
  errorMessage: labelError,
  handleBlur: labelBlur,
} = useField<string>(() => path('label'))
const {
  value: reference,
  errorMessage: referenceError,
  handleBlur: referenceBlur,
} = useField<string>(() => path('reference'))
const {
  value: prixHt,
  errorMessage: prixHtError,
  handleBlur: prixHtBlur,
} = useField<string>(() => path('prixHt'))
const {
  value: stock,
  errorMessage: stockError,
  handleBlur: stockBlur,
} = useField<string>(() => path('stock'))
const {
  value: colisage,
  errorMessage: colisageError,
  handleBlur: colisageBlur,
} = useField<string>(() => path('colisage'))

/** Prix TTC indicatif, recalculé à chaque frappe pour l'aperçu admin. */
const prixTtc = computed(() => {
  const ht = Number(prixHt.value)
  const tva = Number(props.tauxTva)
  if (isNaN(ht) || isNaN(tva) || ht <= 0) return '—'
  return (ht * (1 + tva / 100)).toFixed(2)
})
</script>

<template>
  <div
    data-testid="variant-row"
    class="grid grid-cols-1 items-start gap-3 rounded-lg bg-gray-50 p-3 sm:grid-cols-12"
  >
    <div class="sm:col-span-2">
      <Input
        :id="`product-variant-${index}-label`"
        v-model="label"
        label="Libellé"
        type="text"
        placeholder="150cm"
        :error="labelError ?? ''"
        :disabled="locked"
        @blur="labelBlur"
      />
    </div>
    <div class="sm:col-span-3">
      <Input
        :id="`product-variant-${index}-reference`"
        v-model="reference"
        label="Référence"
        type="text"
        :error="referenceError ?? ''"
        :disabled="locked"
        @blur="referenceBlur"
      />
    </div>
    <div class="sm:col-span-2">
      <Input
        :id="`product-variant-${index}-prix-ht`"
        v-model="prixHt"
        label="Prix HT (€)"
        type="number"
        step="0.01"
        min="0.01"
        :error="prixHtError ?? ''"
        :disabled="locked"
        @blur="prixHtBlur"
      />
    </div>
    <div class="flex flex-col gap-1 sm:col-span-2">
      <label class="text-sm font-medium text-dark">Prix TTC (€)</label>
      <div
        :data-testid="`variant-prix-ttc-${index}`"
        class="flex min-h-[44px] items-center rounded-lg border border-gray-200 bg-white px-3 py-2 text-dark"
      >
        {{ prixTtc }}
      </div>
    </div>
    <div class="sm:col-span-1">
      <Input
        :id="`product-variant-${index}-stock`"
        v-model="stock"
        label="Stock"
        type="number"
        min="0"
        step="1"
        :error="stockError ?? ''"
        @blur="stockBlur"
      />
    </div>
    <div class="sm:col-span-1">
      <Input
        :id="`product-variant-${index}-colisage`"
        v-model="colisage"
        label="Colisage"
        type="number"
        min="1"
        step="1"
        :error="colisageError ?? ''"
        :disabled="locked"
        @blur="colisageBlur"
      />
    </div>
    <div class="flex items-end sm:col-span-1 sm:h-full">
      <Button
        v-if="removable"
        type="button"
        data-testid="remove-variant"
        variant="ghost"
        size="icon"
        class="text-red-600 hover:text-red-700"
        :disabled="locked"
        :aria-label="`Supprimer la variante ${index + 1}`"
        @click="$emit('remove')"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 20 20"
          fill="currentColor"
          class="h-5 w-5"
          aria-hidden="true"
        >
          <path
            fill-rule="evenodd"
            d="M8.75 1A2.75 2.75 0 0 0 6 3.75v.443c-.795.077-1.584.176-2.365.298a.75.75 0 1 0 .23 1.482l.149-.022.841 10.518A2.75 2.75 0 0 0 7.596 19h4.807a2.75 2.75 0 0 0 2.742-2.53l.841-10.52.149.023a.75.75 0 0 0 .23-1.482A41.03 41.03 0 0 0 14 4.193V3.75A2.75 2.75 0 0 0 11.25 1h-2.5Z"
            clip-rule="evenodd"
          />
        </svg>
      </Button>
    </div>
  </div>
</template>
