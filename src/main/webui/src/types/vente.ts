export interface Vente {
  id: number
  name: string
  description: string
  status: string
  startDate: string | null
  endDate: string | null
  createdAt: string
}

export interface AdminVente {
  id: number
  name: string
  description: string
  status: string
  startDate: string | null
  endDate: string | null
  createdAt: string
}
