export interface RayonColor {
  bg: string
  border: string
  text: string
}

export const RAYON_COLORS: RayonColor[] = [
  { bg: 'bg-green-100', border: 'border-green-400', text: 'text-green-300' },
  { bg: 'bg-blue-100', border: 'border-blue-400', text: 'text-blue-300' },
  { bg: 'bg-orange-100', border: 'border-orange-400', text: 'text-orange-300' },
  { bg: 'bg-pink-100', border: 'border-pink-400', text: 'text-pink-300' },
  { bg: 'bg-purple-100', border: 'border-purple-400', text: 'text-purple-300' },
  { bg: 'bg-teal-100', border: 'border-teal-400', text: 'text-teal-300' },
]

export function getRayonColor(index: number): RayonColor {
  return RAYON_COLORS[index % RAYON_COLORS.length]!
}

export function toRayonId(rayon: string): string {
  return (
    'rayon-' +
    rayon
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .replace(/\s+/g, '-')
      .replace(/[^a-z0-9-]/g, '')
  )
}
