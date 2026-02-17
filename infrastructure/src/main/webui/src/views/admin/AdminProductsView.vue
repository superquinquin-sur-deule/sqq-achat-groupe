<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import Button from '@/components/ui/Button.vue'
import EmptyState from '@/components/ui/EmptyState.vue'
import Pagination from '@/components/ui/Pagination.vue'
import ProductForm from '@/components/admin/ProductForm.vue'
import ProductImportForm from '@/components/admin/ProductImportForm.vue'
import { useVenteStore } from '@/stores/venteStore'
import { storeToRefs } from 'pinia'
import { useToast } from '@/composables/useToast'
import { useCursorPagination } from '@/composables/useCursorPagination'
import {
  useAdminProductsQuery,
  useCreateProductMutation,
  useUpdateProductMutation,
  useDeleteProductMutation,
  useImportProductsMutation,
} from '@/composables/api/useAdminProductsApi'
import { useVenteHasOrdersQuery } from '@/composables/api/useAdminVentesApi'
import AdminTable from '@/components/admin/AdminTable.vue'
import type {
  AdminProductResponse,
  CreateProductRequest,
  UpdateProductRequest,
} from '@/api/generated/model'

const toast = useToast()
const venteStore = useVenteStore()
const { selectedVenteId } = storeToRefs(venteStore)
const pagination = useCursorPagination()

watch(selectedVenteId, () => pagination.reset())

const { data: pageData, isLoading: loading } = useAdminProductsQuery(
  selectedVenteId,
  pagination.currentCursor,
)
const products = computed(() => pageData.value?.data ?? [])
const pageInfo = computed(() => pageData.value?.pageInfo ?? { endCursor: null, hasNext: false })

const createMutation = useCreateProductMutation(selectedVenteId)
const updateMutation = useUpdateProductMutation(selectedVenteId)
const deleteMutation = useDeleteProductMutation(selectedVenteId)
const importMutation = useImportProductsMutation(selectedVenteId)

const { data: hasOrdersData } = useVenteHasOrdersQuery(selectedVenteId)
const hasOrders = computed(() => hasOrdersData.value === true)

const showForm = ref(false)
const editingProduct = ref<AdminProductResponse | undefined>(undefined)
const submitting = ref(false)
const confirmingDeleteId = ref<number | null>(null)
const showImportForm = ref(false)
const importing = ref(false)
const importFormRef = ref<InstanceType<typeof ProductImportForm> | null>(null)

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
    const response = await importMutation.mutateAsync(file)
    const result = response.data as {
      data: {
        imported: number
        errors: number
        errorDetails: Array<{ line: number; reason: string }>
      }
    }
    const importResult = result.data
    importFormRef.value?.setResult(importResult)
    if (importResult.errors === 0) {
      toast.success(
        `${importResult.imported} produit${importResult.imported > 1 ? 's' : ''} importé${importResult.imported > 1 ? 's' : ''}`,
      )
    } else if (importResult.imported > 0) {
      toast.warning(
        `${importResult.imported} produit${importResult.imported > 1 ? 's' : ''} importé${importResult.imported > 1 ? 's' : ''}, ${importResult.errors} erreur${importResult.errors > 1 ? 's' : ''}`,
      )
    } else {
      toast.error('Aucun produit importé')
    }
  } catch {
    toast.error("Erreur lors de l'import des produits")
  } finally {
    importing.value = false
  }
}

function openEditForm(product: AdminProductResponse) {
  editingProduct.value = product
  showForm.value = true
  confirmingDeleteId.value = null
}

function closeForm() {
  showForm.value = false
  editingProduct.value = undefined
}

async function onSubmit(data: {
  name: string
  description: string
  price: number
  supplier: string
  stock: number
  active: boolean
}) {
  if (!selectedVenteId.value) return
  submitting.value = true
  try {
    if (editingProduct.value) {
      const updateData: UpdateProductRequest = {
        name: data.name,
        description: data.description,
        price: data.price,
        supplier: data.supplier,
        stock: data.stock,
        active: data.active,
      }
      await updateMutation.mutateAsync({ id: editingProduct.value.id, data: updateData })
      toast.success('Produit mis à jour')
    } else {
      const createData: CreateProductRequest = {
        name: data.name,
        description: data.description,
        price: data.price,
        supplier: data.supplier,
        stock: data.stock,
      }
      await createMutation.mutateAsync(createData)
      toast.success('Produit créé')
    }
    closeForm()
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
    await deleteMutation.mutateAsync(id)
    toast.success('Produit supprimé')
    confirmingDeleteId.value = null
  } catch {
    toast.error('Erreur lors de la suppression du produit')
  }
}

async function toggleActive(product: AdminProductResponse) {
  if (!selectedVenteId.value) return
  try {
    const updateData: UpdateProductRequest = {
      name: product.name,
      description: product.description,
      price: product.price,
      supplier: product.supplier,
      stock: product.stock,
      active: !product.active,
    }
    await updateMutation.mutateAsync({ id: product.id, data: updateData })
    toast.success(product.active ? 'Produit désactivé' : 'Produit activé')
  } catch {
    toast.error('Erreur lors de la modification du produit')
  }
}

function formatPrice(price: number): string {
  return (
    price.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' €'
  )
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

    <div
      v-if="hasOrders"
      class="mb-4 rounded-lg border border-blue-200 bg-blue-50 px-4 py-3 text-sm text-blue-800"
    >
      Cette vente contient des commandes. La modification et la suppression des produits sont
      désactivées. Vous pouvez toujours activer ou désactiver un produit.
    </div>

    <div v-if="loading" class="py-12 text-center text-brown">Chargement des produits...</div>

    <EmptyState
      v-else-if="(!products || products.length === 0) && !showForm"
      message="Aucun produit. Ajoutez votre premier produit."
      action-label="Ajouter un produit"
      @action="openCreateForm"
    />

    <AdminTable
      v-else-if="products && products.length > 0"
      :columns="['Nom', 'Prix', 'Fournisseur', 'Stock', 'Statut', 'Actions']"
      caption="Liste des produits"
      dataTestid="product-table"
    >
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
              product.active ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600',
            ]"
          >
            {{ product.active ? 'Actif' : 'Inactif' }}
          </span>
        </td>
        <td class="px-4 py-3">
          <div class="flex gap-2">
            <template v-if="hasOrders">
              <Button
                :variant="product.active ? 'secondary' : 'primary'"
                size="md"
                :aria-label="(product.active ? 'Désactiver ' : 'Activer ') + product.name"
                @click="toggleActive(product)"
              >
                {{ product.active ? 'Désactiver' : 'Activer' }}
              </Button>
            </template>
            <template v-else-if="confirmingDeleteId === product.id">
              <Button
                variant="danger"
                size="md"
                :aria-label="'Confirmer la suppression de ' + product.name"
                @click="confirmDelete(product.id)"
              >
                Supprimer
              </Button>
              <Button variant="ghost" size="md" @click="cancelDelete"> Annuler </Button>
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
    </AdminTable>

    <Pagination
      v-if="products && products.length > 0"
      :has-next="pageInfo.hasNext"
      :has-previous="pagination.hasPrevious.value"
      :page-number="pagination.pageNumber.value"
      :loading="loading"
      @next="pagination.goToNext(pageInfo.endCursor!)"
      @previous="pagination.goToPrevious()"
    />
  </div>
</template>
