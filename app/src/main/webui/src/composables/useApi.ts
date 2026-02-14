import { ref } from 'vue'
import { ApiError } from '@/api/mutator/custom-fetch'

export function useApi<T>(apiFn: () => Promise<T>) {
  const data = ref<T | null>(null) as { value: T | null }
  const error = ref<string | null>(null)
  const loading = ref(false)

  async function execute(): Promise<T | null> {
    loading.value = true
    error.value = null
    try {
      const result = await apiFn()
      data.value = result
      return result
    } catch (e) {
      if (e instanceof ApiError) {
        error.value = e.message
      } else if (e instanceof Error) {
        error.value = e.message
      } else {
        error.value = 'Une erreur inattendue est survenue'
      }
      return null
    } finally {
      loading.value = false
    }
  }

  return { data, error, loading, execute }
}
