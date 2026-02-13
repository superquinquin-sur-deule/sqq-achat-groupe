import { api } from './client'
import type { Vente } from '@/types/vente'

interface VenteListResponse {
  data: Vente[]
}

interface VenteDetailResponse {
  data: Vente
}

export async function fetchVentes(): Promise<Vente[]> {
  const response = await api.get<VenteListResponse>('/api/ventes')
  return response.data
}

export async function fetchVente(venteId: number): Promise<Vente> {
  const response = await api.get<VenteDetailResponse>(`/api/ventes/${venteId}`)
  return response.data
}
