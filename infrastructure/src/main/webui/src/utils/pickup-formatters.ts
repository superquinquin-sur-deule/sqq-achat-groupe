export const PICKUP_MODE_TIME_SLOTS = 'TIME_SLOTS'
export const PICKUP_MODE_DATE_RANGE = 'DATE_RANGE'

interface PickupRange {
  pickupMode?: string | null
  pickupStartDate?: string | null
  pickupEndDate?: string | null
}

/** Une vente sans créneau : le retrait se fait librement sur une plage de jours. */
export function usesTimeSlots(vente: PickupRange | null | undefined): boolean {
  return vente?.pickupMode !== PICKUP_MODE_DATE_RANGE
}

/** « samedi 12 décembre 2026 » — une date nue (YYYY-MM-DD), sans décalage de fuseau. */
export function formatPickupDate(dateStr: string, withYear = true): string {
  return new Date(dateStr + 'T00:00:00').toLocaleDateString('fr-FR', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    ...(withYear ? { year: 'numeric' as const } : {}),
  })
}

/**
 * « du samedi 12 décembre 2026 au mardi 15 décembre 2026 », ou « le … » si un seul jour.
 * `withYear = false` pour les bandeaux, qui n'affichent pas l'année.
 */
export function formatPickupRange(
  pickupStartDate: string | null | undefined,
  pickupEndDate: string | null | undefined,
  withYear = true,
): string {
  if (!pickupStartDate || !pickupEndDate) return ''
  if (pickupStartDate === pickupEndDate) {
    return `le ${formatPickupDate(pickupStartDate, withYear)}`
  }
  return `du ${formatPickupDate(pickupStartDate, withYear)} au ${formatPickupDate(pickupEndDate, withYear)}`
}

/** Forme courte « 12/12/2026 – 15/12/2026 » pour les tableaux admin. */
export function formatPickupRangeShort(
  pickupStartDate: string | null | undefined,
  pickupEndDate: string | null | undefined,
): string {
  if (!pickupStartDate || !pickupEndDate) return ''
  const short = (d: string) => d.split('-').reverse().join('/')
  return `${short(pickupStartDate)} – ${short(pickupEndDate)}`
}
