<script setup lang="ts">
import { ref } from 'vue'
import { cva } from 'class-variance-authority'
import Button from '@/components/ui/Button.vue'

interface ImportError {
  line: number
  reason: string
}

interface ImportResult {
  imported: number
  errors: number
  errorDetails: ImportError[]
}

defineProps<{
  loading?: boolean
}>()

const emit = defineEmits<{
  submit: [file: File]
  cancel: []
}>()

const selectedFile = ref<File | null>(null)
const isDragging = ref(false)
const importResult = ref<ImportResult | null>(null)

const dropZoneVariants = cva(
  'flex flex-col items-center rounded-lg border-2 border-dashed p-8 transition-colors',
  {
    variants: {
      dragging: {
        true: 'border-primary bg-primary/5',
        false: 'border-gray-300',
      },
    },
    defaultVariants: { dragging: false },
  },
)

function onFileChange(event: Event) {
  const input = event.target as HTMLInputElement
  if (input.files && input.files.length > 0) {
    selectedFile.value = input.files[0]!
    importResult.value = null
  }
}

function removeFile() {
  selectedFile.value = null
  importResult.value = null
}

function onDragOver(event: DragEvent) {
  event.preventDefault()
  isDragging.value = true
}

function onDragLeave() {
  isDragging.value = false
}

function onDrop(event: DragEvent) {
  event.preventDefault()
  isDragging.value = false
  if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
    selectedFile.value = event.dataTransfer.files[0]!
    importResult.value = null
  }
}

function openFilePicker() {
  const input = document.getElementById('csv-file-input') as HTMLInputElement
  input?.click()
}

function onSubmit() {
  if (selectedFile.value) {
    emit('submit', selectedFile.value)
  }
}

function setResult(result: ImportResult) {
  importResult.value = result
}

function closeReport() {
  importResult.value = null
}

defineExpose({ setResult })
</script>

<template>
  <div class="rounded-xl border border-gray-200 bg-white p-6" data-testid="import-form">
    <h2 class="mb-4 text-lg font-bold text-dark">Importer des produits</h2>

    <div
      role="button"
      tabindex="0"
      :class="dropZoneVariants({ dragging: isDragging })"
      @dragover="onDragOver"
      @dragleave="onDragLeave"
      @drop="onDrop"
      @click="openFilePicker"
      @keydown.enter="openFilePicker"
      @keydown.space.prevent="openFilePicker"
    >
      <template v-if="!selectedFile">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="mb-2 h-8 w-8 text-brown"
          aria-hidden="true"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M3 16.5v2.25A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75V16.5m-13.5-9L12 3m0 0 4.5 4.5M12 3v13.5"
          />
        </svg>
        <p class="mb-1 text-dark">
          Glissez un fichier CSV ici ou
          <span class="font-medium text-primary underline">Parcourir</span>
        </p>
        <p class="text-sm text-brown/60">
          Format attendu : nom, description, prix_ht, taux_tva, fournisseur, stock, reference, categorie, marque
        </p>
      </template>
      <template v-else>
        <div class="flex items-center gap-2" @click.stop>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="1.5"
            stroke="currentColor"
            class="h-5 w-5 text-brown"
            aria-hidden="true"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z"
            />
          </svg>
          <span class="font-medium text-dark">{{ selectedFile.name }}</span>
          <button
            type="button"
            class="ml-1 min-h-[44px] min-w-[44px] rounded p-1 text-brown hover:text-dark"
            aria-label="Supprimer le fichier sélectionné"
            @click="removeFile"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              class="h-4 w-4"
              aria-hidden="true"
            >
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </template>
    </div>

    <input
      id="csv-file-input"
      type="file"
      accept=".csv,text/csv"
      class="hidden"
      aria-label="Sélectionner un fichier CSV à importer"
      data-testid="import-file-input"
      @change="onFileChange"
    />

    <div class="mt-4 flex gap-2">
      <Button
        variant="primary"
        :disabled="!selectedFile"
        :loading="loading"
        data-testid="import-submit-btn"
        @click="onSubmit"
      >
        Importer
      </Button>
      <Button variant="ghost" @click="emit('cancel')"> Annuler </Button>
    </div>

    <div v-if="importResult" class="mt-6" data-testid="import-report">
      <h3 class="mb-2 text-base font-bold text-dark">Rapport d'import</h3>
      <p class="mb-3 text-dark">
        {{ importResult.imported }} produit{{ importResult.imported > 1 ? 's' : '' }} importé{{
          importResult.imported > 1 ? 's' : ''
        }}
        avec succès.
        <template v-if="importResult.errors > 0">
          {{ importResult.errors }} ligne{{ importResult.errors > 1 ? 's' : '' }} en erreur.
        </template>
      </p>

      <div
        v-if="importResult.errorDetails.length > 0"
        class="overflow-x-auto rounded-lg border border-gray-200"
      >
        <table class="w-full" data-testid="import-error-table">
          <caption class="sr-only">
            Détails des erreurs d'import
          </caption>
          <thead>
            <tr class="bg-surface text-left text-sm text-dark">
              <th class="px-4 py-2 font-medium">Ligne</th>
              <th class="px-4 py-2 font-medium">Motif</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="error in importResult.errorDetails"
              :key="error.line"
              class="border-t border-gray-100 bg-white"
            >
              <td class="px-4 py-2 text-dark">{{ error.line }}</td>
              <td class="px-4 py-2 text-red-600">{{ error.reason }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <Button variant="ghost" class="mt-3" @click="closeReport"> Fermer le rapport </Button>
    </div>
  </div>
</template>
