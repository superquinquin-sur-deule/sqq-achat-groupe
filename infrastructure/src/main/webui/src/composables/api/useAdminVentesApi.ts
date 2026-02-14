import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import {
  getApiAdminVentes,
  postApiAdminVentes,
  putApiAdminVentesId,
  deleteApiAdminVentesId,
  putApiAdminVentesIdActivate,
  putApiAdminVentesIdDeactivate,
} from '@/api/generated/admin-ventes/admin-ventes'
import type { CreateAdminVenteRequest, UpdateAdminVenteRequest } from '@/api/generated/model'

export function useAdminVentesQuery() {
  return useQuery({
    queryKey: queryKeys.admin.ventes.all,
    queryFn: async () => {
      const response = await getApiAdminVentes()
      return response.data.data ?? []
    },
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
