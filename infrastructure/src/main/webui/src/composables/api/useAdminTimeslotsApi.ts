import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import {
  getApiAdminVentesVenteIdTimeslots,
  postApiAdminVentesVenteIdTimeslots,
  putApiAdminVentesVenteIdTimeslotsId,
  deleteApiAdminVentesVenteIdTimeslotsId,
} from '@/api/generated/admin-timeslots/admin-timeslots'
import type { CreateTimeSlotRequest, UpdateTimeSlotRequest } from '@/api/generated/model'

export function useAdminTimeslotsQuery(
  venteId: MaybeRefOrGetter<number | null>,
  cursor: MaybeRefOrGetter<string | null> = null,
) {
  return useQuery({
    queryKey: computed(() => queryKeys.admin.timeslots.list(toValue(venteId)!, toValue(cursor))),
    queryFn: async () => {
      const params: Record<string, unknown> = {}
      const c = toValue(cursor)
      if (c) params.cursor = c
      const response = await getApiAdminVentesVenteIdTimeslots(toValue(venteId)!, params)
      return {
        data: response.data.data ?? [],
        pageInfo: response.data.pageInfo ?? { endCursor: null, hasNext: false },
      }
    },
    enabled: () => toValue(venteId) !== null,
    placeholderData: keepPreviousData,
  })
}

export function useCreateTimeslotMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateTimeSlotRequest) =>
      postApiAdminVentesVenteIdTimeslots(toValue(venteId)!, data),
    onSuccess: () => {
      const id = toValue(venteId)
      if (id !== null) {
        queryClient.invalidateQueries({ queryKey: ['admin', 'timeslots'] })
      }
    },
  })
}

export function useUpdateTimeslotMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateTimeSlotRequest }) =>
      putApiAdminVentesVenteIdTimeslotsId(toValue(venteId)!, id, data),
    onSuccess: () => {
      const vid = toValue(venteId)
      if (vid !== null) {
        queryClient.invalidateQueries({ queryKey: ['admin', 'timeslots'] })
      }
    },
  })
}

export function useDeleteTimeslotMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, force }: { id: number; force?: boolean }) =>
      deleteApiAdminVentesVenteIdTimeslotsId(
        toValue(venteId)!,
        id,
        force ? { force: true } : undefined,
      ),
    onSuccess: () => {
      const vid = toValue(venteId)
      if (vid !== null) {
        queryClient.invalidateQueries({ queryKey: ['admin', 'timeslots'] })
      }
    },
  })
}
