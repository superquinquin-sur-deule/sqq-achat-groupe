import { api } from './client'
import type { Product } from '@/types/product'

interface ProductListResponse {
  data: Product[]
  meta: {
    total: number
    page: number
    pageSize: number
  }
}

interface ProductDetailResponse {
  data: Product
}

export async function fetchProducts(venteId: number): Promise<Product[]> {
  const response = await api.get<ProductListResponse>(`/api/ventes/${venteId}/products`)
  return response.data
}

export async function fetchProduct(venteId: number, id: number): Promise<Product> {
  const response = await api.get<ProductDetailResponse>(`/api/ventes/${venteId}/products/${id}`)
  return response.data
}
