<script setup lang="ts">
import { useForm, useField } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { z } from 'zod'
import { nextTick, onMounted, ref } from 'vue'
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
      price: number
      supplier: string
      stock: number
      active: boolean
      reference: string
      category: string
      brand: string
    },
  ]
  cancel: []
}>()

const isEdit = !!props.product

const schema = toTypedSchema(
  z.object({
    name: z.string().min(1, 'Merci de saisir un nom de produit'),
    description: z.string().optional().default(''),
    price: z
      .string()
      .min(1, 'Merci de saisir un prix')
      .refine((v) => !isNaN(Number(v)) && Number(v) > 0, 'Le prix doit être supérieur à 0'),
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
        price: String(props.product.price),
        supplier: props.product.supplier,
        stock: String(props.product.stock),
        reference: props.product.reference ?? '',
        category: props.product.category ?? '',
        brand: props.product.brand ?? '',
      }
    : {
        name: '',
        description: '',
        price: '',
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
const { value: price, errorMessage: priceError, handleBlur: priceBlur } = useField<string>('price')
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

const firstInput = ref<InstanceType<typeof Input> | null>(null)

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

onMounted(async () => {
  await nextTick()
  const el = document.getElementById('product-name')
  el?.focus()
})

const onSubmit = handleSubmit((values) => {
  emit('submit', {
    name: values.name,
    description: values.description ?? '',
    price: Number(values.price),
    supplier: values.supplier,
    stock: Number(values.stock),
    active: props.product?.active ?? true,
    reference: values.reference,
    category: values.category,
    brand: values.brand,
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
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <Input
          id="product-price"
          v-model="price"
          label="Prix (€)"
          type="number"
          step="0.01"
          min="0.01"
          :error="priceError ?? ''"
          required
          @blur="priceBlur"
        />
        <Input
          id="product-supplier"
          v-model="supplier"
          label="Fournisseur"
          type="text"
          :error="supplierError ?? ''"
          required
          @blur="supplierBlur"
        />
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
      <div class="mt-2 flex gap-2">
        <Button type="submit" variant="primary" :loading="loading">
          {{ isEdit ? 'Enregistrer les modifications' : 'Créer le produit' }}
        </Button>
        <Button variant="ghost" @click="emit('cancel')"> Annuler </Button>
      </div>
    </form>
  </div>
</template>
