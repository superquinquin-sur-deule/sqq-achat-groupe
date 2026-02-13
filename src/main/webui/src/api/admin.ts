import { api, ApiError } from './client'
import type { AdminProduct, CreateProductData, UpdateProductData, ImportResult } from '@/types/product'
import type { AdminTimeSlot, CreateTimeSlotData, UpdateTimeSlotData } from '@/types/timeSlot'
import type { CampaignStatus } from '@/types/campaign'
import type { DashboardStats } from '@/types/dashboard'

export interface UserInfo {
  name: string
  email: string
}

interface UserInfoResponse {
  data: UserInfo
}

interface AdminProductResponse {
  data: AdminProduct
}

interface AdminProductListResponse {
  data: AdminProduct[]
}

export async function fetchCurrentUser(): Promise<UserInfo> {
  const response = await api.get<UserInfoResponse>('/api/admin/me')
  return response.data
}

export async function fetchProducts(venteId: number): Promise<AdminProduct[]> {
  const response = await api.get<AdminProductListResponse>(`/api/admin/products?venteId=${venteId}`)
  return response.data
}

export async function createProduct(data: CreateProductData): Promise<AdminProduct> {
  const response = await api.post<AdminProductResponse>('/api/admin/products', data)
  return response.data
}

export async function updateProduct(id: number, data: UpdateProductData): Promise<AdminProduct> {
  const response = await api.put<AdminProductResponse>(`/api/admin/products/${id}`, data)
  return response.data
}

export async function deleteProduct(id: number): Promise<void> {
  await api.delete('/api/admin/products/' + id)
}

export async function importProducts(venteId: number, file: File): Promise<ImportResult> {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('venteId', String(venteId))

  const response = await fetch('/api/admin/products/import', {
    method: 'POST',
    body: formData,
  })

  if (!response.ok) {
    const contentType = response.headers.get('content-type')
    if (contentType?.includes('application/problem+json')) {
      const problem = await response.json()
      throw new ApiError(problem, response.status)
    }
    throw new ApiError(
      { type: 'about:blank', title: response.statusText, status: response.status },
      response.status,
    )
  }

  const json = await response.json()
  return json.data
}

// --- Time Slots ---

interface AdminTimeSlotResponse {
  data: AdminTimeSlot
}

interface AdminTimeSlotListResponse {
  data: AdminTimeSlot[]
}

export async function fetchTimeSlots(venteId: number): Promise<AdminTimeSlot[]> {
  const response = await api.get<AdminTimeSlotListResponse>(`/api/admin/timeslots?venteId=${venteId}`)
  return response.data
}

export async function createTimeSlot(data: CreateTimeSlotData): Promise<AdminTimeSlot> {
  const response = await api.post<AdminTimeSlotResponse>('/api/admin/timeslots', data)
  return response.data
}

export async function updateTimeSlot(id: number, data: UpdateTimeSlotData): Promise<AdminTimeSlot> {
  const response = await api.put<AdminTimeSlotResponse>(`/api/admin/timeslots/${id}`, data)
  return response.data
}

export async function deleteTimeSlot(id: number, force?: boolean): Promise<void> {
  const query = force ? '?force=true' : ''
  await api.delete(`/api/admin/timeslots/${id}${query}`)
}

// --- Campaign ---

interface CampaignStatusResponse {
  data: CampaignStatus
}

interface CampaignVenteListResponse {
  data: CampaignStatus[]
}

export async function fetchCampaignVentes(): Promise<CampaignStatus[]> {
  const response = await api.get<CampaignVenteListResponse>('/api/admin/campaign/ventes')
  return response.data
}

export async function fetchCampaignStatus(venteId: number): Promise<CampaignStatus> {
  const response = await api.get<CampaignStatusResponse>(`/api/admin/campaign?venteId=${venteId}`)
  return response.data
}

export async function updateCampaignStatus(venteId: number, active: boolean): Promise<CampaignStatus> {
  const response = await api.put<CampaignStatusResponse>('/api/admin/campaign', { venteId, active })
  return response.data
}

// --- Dashboard ---

interface DashboardStatsResponse {
  data: DashboardStats
}

export async function fetchDashboardStats(venteId: number): Promise<DashboardStats> {
  const response = await api.get<DashboardStatsResponse>(`/api/admin/dashboard?venteId=${venteId}`)
  return response.data
}
