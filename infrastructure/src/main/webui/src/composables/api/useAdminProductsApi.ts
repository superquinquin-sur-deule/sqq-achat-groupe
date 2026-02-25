import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import {
  getApiAdminVentesVenteIdProducts,
  postApiAdminVentesVenteIdProducts,
  putApiAdminVentesVenteIdProductsId,
  deleteApiAdminVentesVenteIdProductsId,
  postApiAdminVentesVenteIdProductsImport,
  postApiAdminVentesVenteIdProductsIdImage,
} from '@/api/generated/admin-products/admin-products'
import type { CreateProductRequest, UpdateProductRequest } from '@/api/generated/model'

export function useAdminProductsQuery(
  venteId: MaybeRefOrGetter<number | null>,
  cursor: MaybeRefOrGetter<string | null> = null,
) {
  return useQuery({
    queryKey: computed(() => queryKeys.admin.products.list(toValue(venteId)!, toValue(cursor))),
    queryFn: async () => {
      const params: Record<string, unknown> = {}
      const c = toValue(cursor)
      if (c) params.cursor = c
      const response = await getApiAdminVentesVenteIdProducts(toValue(venteId)!, params)
      return {
        data: response.data.data ?? [],
        pageInfo: response.data.pageInfo ?? { endCursor: null, hasNext: false },
      }
    },
    enabled: () => toValue(venteId) !== null,
    placeholderData: keepPreviousData,
  })
}

export function useCreateProductMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateProductRequest) =>
      postApiAdminVentesVenteIdProducts(toValue(venteId)!, data),
    onSuccess: () => {
      const id = toValue(venteId)
      if (id !== null) {
        queryClient.invalidateQueries({ queryKey: ['admin', 'products'] })
      }
    },
  })
}

export function useUpdateProductMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateProductRequest }) =>
      putApiAdminVentesVenteIdProductsId(toValue(venteId)!, id, data),
    onSuccess: () => {
      const vid = toValue(venteId)
      if (vid !== null) {
        queryClient.invalidateQueries({ queryKey: ['admin', 'products'] })
      }
    },
  })
}

export function useDeleteProductMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteApiAdminVentesVenteIdProductsId(toValue(venteId)!, id),
    onSuccess: () => {
      const vid = toValue(venteId)
      if (vid !== null) {
        queryClient.invalidateQueries({ queryKey: ['admin', 'products'] })
      }
    },
  })
}

export function useUploadProductImageMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ productId, file }: { productId: number; file: File }) =>
      postApiAdminVentesVenteIdProductsIdImage(toValue(venteId)!, productId, { image: file }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'products'] })
    },
  })
}

export function useImportProductsMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (file: File) =>
      postApiAdminVentesVenteIdProductsImport(toValue(venteId)!, { file }),
    onSuccess: () => {
      const vid = toValue(venteId)
      if (vid !== null) {
        queryClient.invalidateQueries({ queryKey: ['admin', 'products'] })
      }
    },
  })
}
