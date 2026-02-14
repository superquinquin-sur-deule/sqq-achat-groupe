import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import {
  getApiAdminProducts,
  postApiAdminProducts,
  putApiAdminProductsId,
  deleteApiAdminProductsId,
  postApiAdminProductsImport,
} from '@/api/generated/admin-products/admin-products'
import type { CreateProductRequest, UpdateProductRequest } from '@/api/generated/model'

export function useAdminProductsQuery(venteId: MaybeRefOrGetter<number | null>) {
  return useQuery({
    queryKey: computed(() => queryKeys.admin.products.list(toValue(venteId)!)),
    queryFn: async () => {
      const response = await getApiAdminProducts({ venteId: toValue(venteId)! })
      return response.data.data ?? []
    },
    enabled: () => toValue(venteId) !== null,
  })
}

export function useCreateProductMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateProductRequest) => postApiAdminProducts(data),
    onSuccess: () => {
      const id = toValue(venteId)
      if (id !== null) {
        queryClient.invalidateQueries({ queryKey: queryKeys.admin.products.list(id) })
      }
    },
  })
}

export function useUpdateProductMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateProductRequest }) =>
      putApiAdminProductsId(id, data),
    onSuccess: () => {
      const vid = toValue(venteId)
      if (vid !== null) {
        queryClient.invalidateQueries({ queryKey: queryKeys.admin.products.list(vid) })
      }
    },
  })
}

export function useDeleteProductMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteApiAdminProductsId(id),
    onSuccess: () => {
      const vid = toValue(venteId)
      if (vid !== null) {
        queryClient.invalidateQueries({ queryKey: queryKeys.admin.products.list(vid) })
      }
    },
  })
}

export function useImportProductsMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (file: File) => postApiAdminProductsImport({ file, venteId: toValue(venteId)! }),
    onSuccess: () => {
      const vid = toValue(venteId)
      if (vid !== null) {
        queryClient.invalidateQueries({ queryKey: queryKeys.admin.products.list(vid) })
      }
    },
  })
}
