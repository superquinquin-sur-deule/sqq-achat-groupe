export interface CartItem {
  productId: number
  /** Identité de la ligne de panier : une variante = une ligne. */
  variantId: number
  /** Vide pour un produit simple (variante unique sans libellé). */
  variantLabel: string
  name: string
  price: number
  supplier: string
  quantity: number
  stock: number
}
