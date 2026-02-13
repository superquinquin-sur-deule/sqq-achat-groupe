import { api } from './client'
import type { TimeSlot } from '@/types/timeSlot'

interface TimeSlotListResponse {
  data: TimeSlot[]
}

export async function fetchTimeSlots(venteId: number): Promise<TimeSlot[]> {
  const response = await api.get<TimeSlotListResponse>(`/api/ventes/${venteId}/timeslots`)
  return response.data
}
