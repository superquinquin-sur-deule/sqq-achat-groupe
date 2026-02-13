import { api } from './client'
import type {
  BackofficeOrder,
  BackofficeOrderDetail,
  PreparationOrder,
  SupplierOrderLine,
} from '@/types/backoffice'

interface BackofficeOrderListResponse {
  data: BackofficeOrder[]
}

interface BackofficeOrderDetailResponse {
  data: BackofficeOrderDetail
}

export async function fetchBackofficeOrders(venteId: number): Promise<BackofficeOrder[]> {
  const response = await api.get<BackofficeOrderListResponse>(`/api/backoffice/orders?venteId=${venteId}`)
  return response.data
}

export async function fetchBackofficeOrderDetail(orderId: number): Promise<BackofficeOrderDetail> {
  const response = await api.get<BackofficeOrderDetailResponse>(`/api/backoffice/orders/${orderId}`)
  return response.data
}

interface SupplierOrderListResponse {
  data: SupplierOrderLine[]
}

export async function fetchSupplierOrder(venteId: number): Promise<SupplierOrderLine[]> {
  const response = await api.get<SupplierOrderListResponse>(`/api/backoffice/supplier-orders?venteId=${venteId}`)
  return response.data
}

interface PreparationListResponse {
  data: PreparationOrder[]
}

export async function fetchPreparationList(venteId: number): Promise<PreparationOrder[]> {
  const response = await api.get<PreparationListResponse>(`/api/backoffice/preparation?venteId=${venteId}`)
  return response.data
}

export async function markOrderAsPickedUp(orderId: number): Promise<void> {
  await api.put<void>(`/api/backoffice/orders/${orderId}/pickup`)
}
