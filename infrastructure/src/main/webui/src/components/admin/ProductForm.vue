<script setup lang="ts">
import { useForm, useField } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { z } from 'zod'
import { computed, nextTick, onMounted, ref } from 'vue'
import { cva } from 'class-variance-authority'
import Input from '@/components/ui/Input.vue'
import Button from '@/components/ui/Button.vue'
import type { AdminProductResponse } from '@/api/generated/model'

const props = defineProps<{
  product?: AdminProductResponse
  loading?: boolean
}>()

const emit = defineEmits<{
  submit: [
    data: {
      name: string
      description: string
      prixHt: number
      tauxTva: number
      supplier: string
      stock: number
      active: boolean
      reference: string
      category: string
      brand: string
      imageFile?: File
    },
  ]
  cancel: []
}>()

const isEdit = !!props.product

const schema = toTypedSchema(
  z.object({
    name: z.string().min(1, 'Merci de saisir un nom de produit'),
    description: z.string().optional().default(''),
    prixHt: z
      .string()
      .min(1, 'Merci de saisir un prix HT')
      .refine((v) => !isNaN(Number(v)) && Number(v) > 0, 'Le prix HT doit être supérieur à 0'),
    tauxTva: z
      .string()
      .min(1, 'Merci de saisir un taux de TVA')
      .refine(
        (v) => !isNaN(Number(v)) && Number(v) >= 0,
        'Le taux de TVA ne peut pas être négatif',
      ),
    supplier: z.string().min(1, 'Merci de saisir un fournisseur'),
    stock: z
      .string()
      .min(1, 'Merci de saisir un stock')
      .refine(
        (v) => !isNaN(Number(v)) && Number(v) >= 0 && Number.isInteger(Number(v)),
        'Le stock ne peut pas être négatif',
      ),
    reference: z.string().min(1, 'Merci de saisir une référence'),
    category: z.string().min(1, 'Merci de saisir une catégorie'),
    brand: z.string().min(1, 'Merci de saisir une marque'),
  }),
)

const { handleSubmit } = useForm({
  validationSchema: schema,
  initialValues: props.product
    ? {
        name: props.product.name,
        description: props.product.description ?? '',
        prixHt: String(props.product.prixHt),
        tauxTva: String(props.product.tauxTva),
        supplier: props.product.supplier,
        stock: String(props.product.stock),
        reference: props.product.reference ?? '',
        category: props.product.category ?? '',
        brand: props.product.brand ?? '',
      }
    : {
        name: '',
        description: '',
        prixHt: '',
        tauxTva: '5.50',
        supplier: '',
        stock: '0',
        reference: '',
        category: '',
        brand: '',
      },
})

const { value: name, errorMessage: nameError, handleBlur: nameBlur } = useField<string>('name')
const {
  value: description,
  errorMessage: descriptionError,
  handleBlur: descriptionBlur,
} = useField<string>('description')
const {
  value: prixHt,
  errorMessage: prixHtError,
  handleBlur: prixHtBlur,
} = useField<string>('prixHt')
const {
  value: tauxTva,
  errorMessage: tauxTvaError,
  handleBlur: tauxTvaBlur,
} = useField<string>('tauxTva')
const {
  value: supplier,
  errorMessage: supplierError,
  handleBlur: supplierBlur,
} = useField<string>('supplier')
const { value: stock, errorMessage: stockError, handleBlur: stockBlur } = useField<string>('stock')
const {
  value: reference,
  errorMessage: referenceError,
  handleBlur: referenceBlur,
} = useField<string>('reference')
const {
  value: category,
  errorMessage: categoryError,
  handleBlur: categoryBlur,
} = useField<string>('category')
const { value: brand, errorMessage: brandError, handleBlur: brandBlur } = useField<string>('brand')

const imageFile = ref<File | undefined>(undefined)
const imagePreview = ref<string | undefined>(props.product?.imageUrl ?? undefined)
const firstInput = ref<InstanceType<typeof Input> | null>(null)

const prixTtc = computed(() => {
  const ht = Number(prixHt.value)
  const tva = Number(tauxTva.value)
  if (isNaN(ht) || isNaN(tva) || ht <= 0) return null
  return (ht * (1 + tva / 100)).toFixed(2)
})

const textareaVariants = cva(
  'min-h-[44px] rounded-lg border px-3 py-2 text-dark transition-colors focus:outline-2 focus:outline-offset-2 focus:outline-dark',
  {
    variants: {
      error: {
        true: 'border-red-600 focus:border-red-600',
        false: 'border-gray-300 focus:border-primary',
      },
    },
    defaultVariants: { error: false },
  },
)

function onImageChange(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (file) {
    imageFile.value = file
    imagePreview.value = URL.createObjectURL(file)
  }
}

onMounted(async () => {
  await nextTick()
  const el = document.getElementById('product-name')
  el?.focus()
})

const onSubmit = handleSubmit((values) => {
  emit('submit', {
    name: values.name,
    description: values.description ?? '',
    prixHt: Number(values.prixHt),
    tauxTva: Number(values.tauxTva),
    supplier: values.supplier,
    stock: Number(values.stock),
    active: props.product?.active ?? true,
    reference: values.reference,
    category: values.category,
    brand: values.brand,
    imageFile: imageFile.value,
  })
})
</script>

<template>
  <div class="rounded-xl border border-gray-200 bg-white p-6" data-testid="product-form">
    <h2 class="mb-4 text-lg font-bold text-dark">
      {{ isEdit ? 'Modifier le produit' : 'Nouveau produit' }}
    </h2>
    <form class="flex flex-col gap-4" @submit.prevent="onSubmit">
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <Input
          id="product-name"
          ref="firstInput"
          v-model="name"
          label="Nom"
          type="text"
          :error="nameError ?? ''"
          required
          @blur="nameBlur"
        />
        <Input
          id="product-reference"
          v-model="reference"
          label="Référence"
          type="text"
          :error="referenceError ?? ''"
          required
          @blur="referenceBlur"
        />
      </div>
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <Input
          id="product-category"
          v-model="category"
          label="Catégorie"
          type="text"
          :error="categoryError ?? ''"
          required
          @blur="categoryBlur"
        />
        <Input
          id="product-brand"
          v-model="brand"
          label="Marque"
          type="text"
          :error="brandError ?? ''"
          required
          @blur="brandBlur"
        />
      </div>
      <div class="flex flex-col gap-1">
        <label for="product-description" class="text-sm font-medium text-dark">Description</label>
        <textarea
          id="product-description"
          v-model="description"
          rows="3"
          :aria-describedby="descriptionError ? 'product-description-error' : undefined"
          :aria-invalid="!!descriptionError"
          :class="textareaVariants({ error: !!descriptionError })"
          @blur="descriptionBlur"
        />
        <p
          v-if="descriptionError"
          id="product-description-error"
          class="text-sm text-red-600"
          role="alert"
        >
          {{ descriptionError }}
        </p>
      </div>
      <div class="flex flex-col gap-1">
        <label for="product-image" class="text-sm font-medium text-dark">Image du produit</label>
        <div class="flex items-center gap-4">
          <img
            v-if="imagePreview"
            :src="imagePreview"
            alt="Aperçu"
            class="h-20 w-20 rounded-lg border border-gray-200 object-cover"
          />
          <div
            v-else
            class="flex h-20 w-20 items-center justify-center rounded-lg border border-dashed border-gray-300 bg-gray-50"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1" stroke="currentColor" class="h-8 w-8 text-gray-400">
              <path stroke-linecap="round" stroke-linejoin="round" d="m2.25 15.75 5.159-5.159a2.25 2.25 0 0 1 3.182 0l5.159 5.159m-1.5-1.5 1.409-1.409a2.25 2.25 0 0 1 3.182 0l2.909 2.909M3.75 21h16.5A2.25 2.25 0 0 0 22.5 19.5V4.5a2.25 2.25 0 0 0-2.25-2.25H3.75A2.25 2.25 0 0 0 1.5 4.5v15a2.25 2.25 0 0 0 2.25 2.25Z" />
            </svg>
          </div>
          <input
            id="product-image"
            type="file"
            accept="image/jpeg,image/png,image/webp"
            class="text-sm text-dark file:mr-3 file:rounded-lg file:border-0 file:bg-primary file:px-3 file:py-2 file:text-sm file:font-medium file:text-dark hover:file:bg-primary/80"
            @change="onImageChange"
          />
        </div>
      </div>
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-4">
        <Input
          id="product-prix-ht"
          v-model="prixHt"
          label="Prix HT (€)"
          type="number"
          step="0.01"
          min="0.01"
          :error="prixHtError ?? ''"
          required
          @blur="prixHtBlur"
        />
        <Input
          id="product-taux-tva"
          v-model="tauxTva"
          label="TVA (%)"
          type="number"
          step="0.01"
          min="0"
          :error="tauxTvaError ?? ''"
          required
          @blur="tauxTvaBlur"
        />
        <div class="flex flex-col gap-1">
          <label class="text-sm font-medium text-dark">Prix TTC (€)</label>
          <div
            class="flex min-h-[44px] items-center rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-dark"
          >
            {{ prixTtc ?? '—' }}
          </div>
        </div>
        <Input
          id="product-stock"
          v-model="stock"
          label="Stock"
          type="number"
          min="0"
          step="1"
          :error="stockError ?? ''"
          required
          @blur="stockBlur"
        />
      </div>
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <Input
          id="product-supplier"
          v-model="supplier"
          label="Fournisseur"
          type="text"
          :error="supplierError ?? ''"
          required
          @blur="supplierBlur"
        />
      </div>
      <div class="mt-2 flex gap-2">
        <Button type="submit" variant="primary" :loading="loading">
          {{ isEdit ? 'Enregistrer les modifications' : 'Créer le produit' }}
        </Button>
        <Button variant="ghost" @click="emit('cancel')"> Annuler </Button>
      </div>
    </form>
  </div>
</template>
