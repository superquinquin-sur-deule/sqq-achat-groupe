<script setup lang="ts">
import { useForm, useField, useFieldArray } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { z } from 'zod'
import { nextTick, onMounted, ref } from 'vue'
import { cva } from 'class-variance-authority'
import Input from '@/components/ui/Input.vue'
import Button from '@/components/ui/Button.vue'
import ProductVariantRow from '@/components/admin/ProductVariantRow.vue'
import type { AdminProductResponse } from '@/api/generated/model'

export interface VariantFormValue {
  id?: number
  label: string
  reference: string
  prixHt: number
  stock: number
  colisage?: number
}

const props = defineProps<{
  product?: AdminProductResponse
  loading?: boolean
  hasOrders?: boolean
}>()

const emit = defineEmits<{
  submit: [
    data: {
      name: string
      description: string
      tauxTva: number
      supplier: string
      active: boolean
      category: string
      brand: string
      importantNote: string
      variants: VariantFormValue[]
      imageFile?: File
    },
  ]
  cancel: []
}>()

const isEdit = !!props.product
const fieldsLocked = !!(props.hasOrders && isEdit)

const variantSchema = z.object({
  id: z.number().optional(),
  label: z
    .string()
    .max(100, 'Le libellé ne peut pas dépasser 100 caractères')
    .optional()
    .default(''),
  reference: z.string().min(1, 'Merci de saisir une référence'),
  prixHt: z
    .string()
    .min(1, 'Merci de saisir un prix HT')
    .refine((v) => !isNaN(Number(v)) && Number(v) > 0, 'Le prix HT doit être supérieur à 0'),
  stock: z
    .string()
    .min(1, 'Merci de saisir un stock')
    .refine(
      (v) => !isNaN(Number(v)) && Number(v) >= 0 && Number.isInteger(Number(v)),
      'Le stock ne peut pas être négatif',
    ),
  colisage: z
    .string()
    .optional()
    .default('')
    .refine(
      (v) => v === '' || (!isNaN(Number(v)) && Number(v) >= 1 && Number.isInteger(Number(v))),
      'Le colisage doit être un entier supérieur ou égal à 1',
    ),
})

const schema = toTypedSchema(
  z.object({
    name: z.string().min(1, 'Merci de saisir un nom de produit'),
    description: z.string().optional().default(''),
    tauxTva: z
      .string()
      .min(1, 'Merci de saisir un taux de TVA')
      .refine(
        (v) => !isNaN(Number(v)) && Number(v) >= 0,
        'Le taux de TVA ne peut pas être négatif',
      ),
    supplier: z.string().min(1, 'Merci de saisir un fournisseur'),
    category: z.string().min(1, 'Merci de saisir une catégorie'),
    brand: z.string().min(1, 'Merci de saisir une marque'),
    importantNote: z
      .string()
      .max(500, 'La mention importante ne peut pas dépasser 500 caractères')
      .optional()
      .default(''),
    variants: z
      .array(variantSchema)
      .min(1, 'Un produit doit avoir au moins une variante')
      .superRefine((variants, ctx) => {
        if (variants.length > 1 && variants.some((v) => !v.label?.trim())) {
          ctx.addIssue({
            code: z.ZodIssueCode.custom,
            message: 'Chaque variante doit avoir un libellé (ex. 150cm)',
          })
        }
        const references = variants.map((v) => v.reference?.trim()).filter(Boolean)
        if (new Set(references).size !== references.length) {
          ctx.addIssue({
            code: z.ZodIssueCode.custom,
            message: 'Les références des variantes doivent être différentes',
          })
        }
      }),
  }),
)

const emptyVariant = () => ({ label: '', reference: '', prixHt: '', stock: '0', colisage: '' })

const { handleSubmit, errors } = useForm({
  validationSchema: schema,
  initialValues: props.product
    ? {
        name: props.product.name,
        description: props.product.description ?? '',
        tauxTva: String(props.product.tauxTva),
        supplier: props.product.supplier,
        category: props.product.category ?? '',
        brand: props.product.brand ?? '',
        importantNote: props.product.importantNote ?? '',
        variants: (props.product.variants ?? []).map((v) => ({
          id: v.id,
          label: v.label ?? '',
          reference: v.reference ?? '',
          prixHt: String(v.prixHt),
          stock: String(v.stock),
          colisage: v.colisage != null ? String(v.colisage) : '',
        })),
      }
    : {
        name: '',
        description: '',
        tauxTva: '5.50',
        supplier: '',
        category: '',
        brand: '',
        importantNote: '',
        variants: [emptyVariant()],
      },
})

const { value: name, errorMessage: nameError, handleBlur: nameBlur } = useField<string>('name')
const {
  value: description,
  errorMessage: descriptionError,
  handleBlur: descriptionBlur,
} = useField<string>('description')
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
const {
  value: category,
  errorMessage: categoryError,
  handleBlur: categoryBlur,
} = useField<string>('category')
const { value: brand, errorMessage: brandError, handleBlur: brandBlur } = useField<string>('brand')
const {
  value: importantNote,
  errorMessage: importantNoteError,
  handleBlur: importantNoteBlur,
} = useField<string>('importantNote')

const {
  fields: variantFields,
  push: pushVariant,
  remove: removeVariant,
} = useFieldArray('variants')

const imageFile = ref<File | undefined>(undefined)
const imagePreview = ref<string | undefined>(props.product?.imageUrl ?? undefined)
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
    tauxTva: Number(values.tauxTva),
    supplier: values.supplier,
    active: props.product?.active ?? true,
    category: values.category,
    brand: values.brand,
    importantNote: values.importantNote ?? '',
    variants: values.variants.map((v) => ({
      id: v.id,
      label: (v.label ?? '').trim(),
      reference: v.reference,
      prixHt: Number(v.prixHt),
      stock: Number(v.stock),
      colisage: v.colisage ? Number(v.colisage) : undefined,
    })),
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
          :disabled="fieldsLocked"
          required
          @blur="nameBlur"
        />
        <Input
          id="product-category"
          v-model="category"
          label="Catégorie"
          type="text"
          :error="categoryError ?? ''"
          :disabled="fieldsLocked"
          required
          @blur="categoryBlur"
        />
      </div>
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <Input
          id="product-brand"
          v-model="brand"
          label="Marque"
          type="text"
          :error="brandError ?? ''"
          :disabled="fieldsLocked"
          required
          @blur="brandBlur"
        />
        <Input
          id="product-supplier"
          v-model="supplier"
          label="Fournisseur"
          type="text"
          :error="supplierError ?? ''"
          :disabled="fieldsLocked"
          required
          @blur="supplierBlur"
        />
        <Input
          id="product-taux-tva"
          v-model="tauxTva"
          label="TVA (%)"
          type="number"
          step="0.01"
          min="0"
          :error="tauxTvaError ?? ''"
          :disabled="fieldsLocked"
          required
          @blur="tauxTvaBlur"
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
        <label for="product-important-note" class="text-sm font-medium text-dark">
          Mention importante
        </label>
        <textarea
          id="product-important-note"
          v-model="importantNote"
          rows="2"
          placeholder="Ex : le poids est approximatif et peut varier à la réception"
          :aria-describedby="
            importantNoteError ? 'product-important-note-error' : 'product-important-note-hint'
          "
          :aria-invalid="!!importantNoteError"
          :class="textareaVariants({ error: !!importantNoteError })"
          @blur="importantNoteBlur"
        />
        <p
          v-if="importantNoteError"
          id="product-important-note-error"
          class="text-sm text-red-600"
          role="alert"
        >
          {{ importantNoteError }}
        </p>
        <p v-else id="product-important-note-hint" class="text-sm text-brown/60">
          Mise en avant sur la fiche produit du catalogue. Laissez vide si aucune mention.
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
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1"
              stroke="currentColor"
              class="h-8 w-8 text-gray-400"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="m2.25 15.75 5.159-5.159a2.25 2.25 0 0 1 3.182 0l5.159 5.159m-1.5-1.5 1.409-1.409a2.25 2.25 0 0 1 3.182 0l2.909 2.909M3.75 21h16.5A2.25 2.25 0 0 0 22.5 19.5V4.5a2.25 2.25 0 0 0-2.25-2.25H3.75A2.25 2.25 0 0 0 1.5 4.5v15a2.25 2.25 0 0 0 2.25 2.25Z"
              />
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

      <fieldset
        class="flex flex-col gap-3 rounded-lg border border-gray-200 p-4"
        data-testid="variants-section"
      >
        <legend class="px-1 text-sm font-medium text-dark">Variantes</legend>
        <p class="text-sm text-brown/60">
          Laissez le libellé vide pour un produit sans déclinaison. Ajoutez une ligne par taille ou
          format : chacune a sa propre référence, son prix et son stock.
        </p>

        <ProductVariantRow
          v-for="(field, index) in variantFields"
          :key="field.key"
          :index="index"
          :locked="fieldsLocked"
          :removable="variantFields.length > 1"
          :taux-tva="tauxTva"
          @remove="removeVariant(index)"
        />

        <p v-if="errors.variants" class="text-sm text-red-600" role="alert">
          {{ errors.variants }}
        </p>

        <div>
          <Button
            type="button"
            data-testid="add-variant"
            variant="secondary"
            @click="pushVariant(emptyVariant())"
          >
            Ajouter une variante
          </Button>
        </div>
      </fieldset>

      <div class="mt-2 flex gap-2">
        <Button type="submit" variant="primary" :loading="loading">
          {{ isEdit ? 'Enregistrer les modifications' : 'Créer le produit' }}
        </Button>
        <Button variant="ghost" @click="emit('cancel')"> Annuler </Button>
      </div>
    </form>
  </div>
</template>
