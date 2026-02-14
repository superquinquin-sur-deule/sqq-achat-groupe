export function formatPrice(amount: number): string {
  return (
    amount.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' €'
  )
}

export function statusLabel(status: string): string {
  return status === 'PICKED_UP' ? 'Récupéré' : 'Payé'
}

export function statusClasses(status: string): string {
  return status === 'PICKED_UP' ? 'bg-blue-100 text-blue-800' : 'bg-green-100 text-green-800'
}
