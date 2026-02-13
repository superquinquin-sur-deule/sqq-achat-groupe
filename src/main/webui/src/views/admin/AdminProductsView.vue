<script setup lang="ts">
import { ref, watch } from 'vue'
import Button from '@/components/ui/Button.vue'
import ProductForm from '@/components/admin/ProductForm.vue'
import ProductImportForm from '@/components/admin/ProductImportForm.vue'
import { fetchProducts, createProduct, updateProduct, deleteProduct, importProducts } from '@/api/admin'
import { useVenteStore } from '@/stores/venteStore'
import { storeToRefs } from 'pinia'
import { useToast } from '@/composables/useToast'
import type { AdminProduct, CreateProductData, UpdateProductData } from '@/types/product'

const toast = useToast()
const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)

const products = ref<AdminProduct[]>([])
const loading = ref(false)
const showForm = ref(false)
const editingProduct = ref<AdminProduct | undefined>(undefined)
const submitting = ref(false)
const confirmingDeleteId = ref<number | null>(null)
const showImportForm = ref(false)
const importing = ref(false)
const importFormRef = ref<InstanceType<typeof ProductImportForm> | null>(null)

async function loadData(venteId: number) {
  loading.value = true
  try {
    products.value = await fetchProducts(venteId)
  } catch {
    toast.error('Erreur lors du chargement des produits')
  } finally {
    loading.value = false
  }
}

watch(selectedVenteId, (id) => { if (id) loadData(id) }, { immediate: true })

function openCreateForm() {
  editingProduct.value = undefined
  showForm.value = true
  showImportForm.value = false
  confirmingDeleteId.value = null
}

function openImportForm() {
  showImportForm.value = true
  showForm.value = false
  confirmingDeleteId.value = null
}

function closeImportForm() {
  showImportForm.value = false
}

async function onImportSubmit(file: File) {
  if (!selectedVenteId.value) return
  importing.value = true
  try {
    const result = await importProducts(selectedVenteId.value, file)
    importFormRef.value?.setResult(result)
    if (result.errors === 0) {
      toast.success(`${result.imported} produit${result.imported > 1 ? 's' : ''} importé${result.imported > 1 ? 's' : ''}`)
    } else if (result.imported > 0) {
      toast.warning(`${result.imported} produit${result.imported > 1 ? 's' : ''} importé${result.imported > 1 ? 's' : ''}, ${result.errors} erreur${result.errors > 1 ? 's' : ''}`)
    } else {
      toast.error('Aucun produit importé')
    }
    products.value = await fetchProducts(selectedVenteId.value)
  } catch {
    toast.error('Erreur lors de l\'import des produits')
  } finally {
    importing.value = false
  }
}

function openEditForm(product: AdminProduct) {
  editingProduct.value = product
  showForm.value = true
  confirmingDeleteId.value = null
}

function closeForm() {
  showForm.value = false
  editingProduct.value = undefined
}

async function onSubmit(data: { name: string; description: string; price: number; supplier: string; stock: number; active: boolean }) {
  if (!selectedVenteId.value) return
  submitting.value = true
  try {
    if (editingProduct.value) {
      const updateData: UpdateProductData = {
        name: data.name,
        description: data.description,
        price: data.price,
        supplier: data.supplier,
        stock: data.stock,
        active: data.active,
      }
      await updateProduct(editingProduct.value.id, updateData)
      toast.success('Produit mis à jour')
    } else {
      const createData: CreateProductData = {
        venteId: selectedVenteId.value,
        name: data.name,
        description: data.description,
        price: data.price,
        supplier: data.supplier,
        stock: data.stock,
      }
      await createProduct(createData)
      toast.success('Produit créé')
    }
    closeForm()
    products.value = await fetchProducts(selectedVenteId.value)
  } catch {
    toast.error('Erreur lors de la sauvegarde du produit')
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

async function confirmDelete(id: number) {
  if (!selectedVenteId.value) return
  try {
    await deleteProduct(id)
    toast.success('Produit supprimé')
    confirmingDeleteId.value = null
    products.value = await fetchProducts(selectedVenteId.value)
  } catch {
    toast.error('Erreur lors de la suppression du produit')
  }
}

function formatPrice(price: number): string {
  return price.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' €'
}
</script>

<template>
  <div>
    <div class="mb-6 flex items-center justify-between">
      <h1 class="text-2xl font-bold text-dark">Produits</h1>
      <div class="flex gap-3">
        <Button variant="secondary" data-testid="import-btn" @click="openImportForm">
          Importer des produits
        </Button>
        <Button variant="primary" data-testid="add-product-btn" @click="openCreateForm">
          Ajouter un produit
        </Button>
      </div>
    </div>

    <ProductImportForm
      v-if="showImportForm"
      ref="importFormRef"
      :loading="importing"
      class="mb-6"
      @submit="onImportSubmit"
      @cancel="closeImportForm"
    />

    <ProductForm
      v-if="showForm"
      :product="editingProduct"
      :loading="submitting"
      class="mb-6"
      @submit="onSubmit"
      @cancel="closeForm"
    />

    <div v-if="loading" class="py-12 text-center text-brown">
      Chargement des produits...
    </div>

    <div v-else-if="products.length === 0 && !showForm" class="rounded-xl border border-gray-200 bg-white p-12 text-center" data-testid="empty-state">
      <p class="mb-4 text-brown">Aucun produit. Ajoutez votre premier produit.</p>
      <Button variant="primary" @click="openCreateForm">
        Ajouter un produit
      </Button>
    </div>

    <div v-else-if="products.length > 0" class="overflow-x-auto rounded-xl border border-gray-200 bg-white">
      <table class="w-full" data-testid="product-table">
        <caption class="sr-only">Liste des produits</caption>
        <thead>
          <tr class="bg-dark text-left text-sm text-white">
            <th class="px-4 py-3 font-medium">Nom</th>
            <th class="px-4 py-3 font-medium">Prix</th>
            <th class="px-4 py-3 font-medium">Fournisseur</th>
            <th class="px-4 py-3 font-medium">Stock</th>
            <th class="px-4 py-3 font-medium">Statut</th>
            <th class="px-4 py-3 font-medium">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="product in products"
            :key="product.id"
            class="border-t border-gray-100 transition-colors hover:bg-surface"
            data-testid="product-row"
          >
            <td class="px-4 py-3 font-medium text-dark">{{ product.name }}</td>
            <td class="px-4 py-3 text-dark">{{ formatPrice(product.price) }}</td>
            <td class="px-4 py-3 text-dark">{{ product.supplier }}</td>
            <td class="px-4 py-3 text-dark">{{ product.stock }}</td>
            <td class="px-4 py-3">
              <span
                :class="[
                  'inline-block rounded-full px-2 py-0.5 text-xs font-medium',
                  product.active
                    ? 'bg-green-100 text-green-700'
                    : 'bg-gray-100 text-gray-600',
                ]"
              >
                {{ product.active ? 'Actif' : 'Inactif' }}
              </span>
            </td>
            <td class="px-4 py-3">
              <div class="flex gap-2">
                <template v-if="confirmingDeleteId === product.id">
                  <Button
                    variant="danger"
                    size="md"
                    :aria-label="'Confirmer la suppression de ' + product.name"
                    @click="confirmDelete(product.id)"
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
                </template>
                <template v-else>
                  <Button
                    variant="secondary"
                    size="md"
                    :aria-label="'Modifier ' + product.name"
                    @click="openEditForm(product)"
                  >
                    Modifier
                  </Button>
                  <Button
                    variant="danger"
                    size="md"
                    :aria-label="'Supprimer ' + product.name"
                    @click="startDelete(product.id)"
                  >
                    Supprimer
                  </Button>
                </template>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
