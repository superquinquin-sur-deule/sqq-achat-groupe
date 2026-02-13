export interface TimeSlot {
  id: number
  date: string
  startTime: string
  endTime: string
  capacity: number
  reserved: number
  remainingPlaces: number
}

export interface AdminTimeSlot {
  id: number
  date: string
  startTime: string
  endTime: string
  capacity: number
  reserved: number
  remainingPlaces: number
}

export interface CreateTimeSlotData {
  venteId: number
  date: string
  startTime: string
  endTime: string
  capacity: number
}

export interface UpdateTimeSlotData {
  date: string
  startTime: string
  endTime: string
  capacity: number
}
