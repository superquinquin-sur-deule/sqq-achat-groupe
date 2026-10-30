export function formatPrice(amount: number): string {
  return (
    amount.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' €'
  )
}

const STATUS_LABELS: Record<string, string> = {
  CONFIRMED: 'Confirmé',
  PICKED_UP: 'Récupéré',
  CANCELLED: 'Annulé',
}

const STATUS_CLASSES: Record<string, string> = {
  CONFIRMED: 'bg-green-100 text-green-800',
  PICKED_UP: 'bg-blue-100 text-blue-800',
  CANCELLED: 'bg-red-100 text-red-800',
}

export function statusLabel(status: string): string {
  return STATUS_LABELS[status] ?? status
}

export function statusClasses(status: string): string {
  return STATUS_CLASSES[status] ?? 'bg-gray-100 text-gray-800'
}

export function formatDateTime(instant: string): string {
  return new Date(instant).toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
