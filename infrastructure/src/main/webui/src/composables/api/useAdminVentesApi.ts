import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import { customFetch } from '@/api/mutator/custom-fetch'
import {
  getApiAdminVentes,
  postApiAdminVentes,
  putApiAdminVentesId,
  deleteApiAdminVentesId,
  putApiAdminVentesIdActivate,
  putApiAdminVentesIdDeactivate,
} from '@/api/generated/admin-ventes/admin-ventes'
import type { CreateAdminVenteRequest, UpdateAdminVenteRequest } from '@/api/generated/model'

export function useAdminVentesQuery(cursor: MaybeRefOrGetter<string | null> = null) {
  return useQuery({
    queryKey: computed(() => queryKeys.admin.ventes.list(toValue(cursor))),
    queryFn: async () => {
      const params: Record<string, unknown> = {}
      const c = toValue(cursor)
      if (c) params.cursor = c
      const response = await getApiAdminVentes(params)
      return {
        data: response.data.data ?? [],
        pageInfo: response.data.pageInfo ?? { endCursor: null, hasNext: false },
      }
    },
    placeholderData: keepPreviousData,
  })
}

export function useCreateVenteMutation() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateAdminVenteRequest) => postApiAdminVentes(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.admin.ventes.all })
      queryClient.invalidateQueries({ queryKey: queryKeys.ventes.all })
    },
  })
}

export function useUpdateVenteMutation() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateAdminVenteRequest }) =>
      putApiAdminVentesId(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.admin.ventes.all })
      queryClient.invalidateQueries({ queryKey: queryKeys.ventes.all })
    },
  })
}

export function useDeleteVenteMutation() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteApiAdminVentesId(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.admin.ventes.all })
      queryClient.invalidateQueries({ queryKey: queryKeys.ventes.all })
    },
  })
}

export function useActivateVenteMutation() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => putApiAdminVentesIdActivate(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.admin.ventes.all })
      queryClient.invalidateQueries({ queryKey: queryKeys.ventes.all })
    },
  })
}

export function useDeactivateVenteMutation() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => putApiAdminVentesIdDeactivate(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.admin.ventes.all })
      queryClient.invalidateQueries({ queryKey: queryKeys.ventes.all })
    },
  })
}

export function useVenteHasOrdersQuery(venteId: MaybeRefOrGetter<number | null>) {
  return useQuery({
    queryKey: computed(() => queryKeys.admin.ventes.hasOrders(toValue(venteId)!)),
    queryFn: async () => {
      const response = await customFetch<{ data: { data: boolean } }>(
        `/api/admin/ventes/${toValue(venteId)}/has-orders`,
        { method: 'GET' },
      )
      return response.data.data
    },
    enabled: () => toValue(venteId) !== null,
  })
}
