export interface Product {
  id: number
  name: string
  description: string
  price: number
  supplier: string
  stock: number
}

export interface AdminProduct extends Product {
  venteId: number
  active: boolean
}

export interface CreateProductData {
  venteId: number
  name: string
  description: string
  price: number
  supplier: string
  stock: number
}

export interface UpdateProductData {
  name: string
  description: string
  price: number
  supplier: string
  stock: number
  active: boolean
}

export interface ImportResult {
  imported: number
  errors: number
  errorDetails: ImportError[]
}

export interface ImportError {
  line: number
  reason: string
}
