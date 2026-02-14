import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import {
  getApiAdminTimeslots,
  postApiAdminTimeslots,
  putApiAdminTimeslotsId,
  deleteApiAdminTimeslotsId,
} from '@/api/generated/admin-timeslots/admin-timeslots'
import type { CreateTimeSlotRequest, UpdateTimeSlotRequest } from '@/api/generated/model'

export function useAdminTimeslotsQuery(venteId: MaybeRefOrGetter<number | null>) {
  return useQuery({
    queryKey: computed(() => queryKeys.admin.timeslots.list(toValue(venteId)!)),
    queryFn: async () => {
      const response = await getApiAdminTimeslots({ venteId: toValue(venteId)! })
      return response.data.data ?? []
    },
    enabled: () => toValue(venteId) !== null,
  })
}

export function useCreateTimeslotMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateTimeSlotRequest) => postApiAdminTimeslots(data),
    onSuccess: () => {
      const id = toValue(venteId)
      if (id !== null) {
        queryClient.invalidateQueries({ queryKey: queryKeys.admin.timeslots.list(id) })
      }
    },
  })
}

export function useUpdateTimeslotMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateTimeSlotRequest }) =>
      putApiAdminTimeslotsId(id, data),
    onSuccess: () => {
      const vid = toValue(venteId)
      if (vid !== null) {
        queryClient.invalidateQueries({ queryKey: queryKeys.admin.timeslots.list(vid) })
      }
    },
  })
}

export function useDeleteTimeslotMutation(venteId: MaybeRefOrGetter<number | null>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, force }: { id: number; force?: boolean }) =>
      deleteApiAdminTimeslotsId(id, force ? { force: true } : undefined),
    onSuccess: () => {
      const vid = toValue(venteId)
      if (vid !== null) {
        queryClient.invalidateQueries({ queryKey: queryKeys.admin.timeslots.list(vid) })
      }
    },
  })
}
