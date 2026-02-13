import { ref } from 'vue'
import { fetchCurrentUser, type UserInfo } from '@/api/admin'

const user = ref<UserInfo | null>(null)
const loading = ref(false)
const authenticated = ref(false)

export function useAuth() {
  async function checkAuth(): Promise<boolean> {
    if (authenticated.value) return true

    loading.value = true
    try {
      user.value = await fetchCurrentUser()
      authenticated.value = true
      return true
    } catch {
      authenticated.value = false
      user.value = null
      return false
    } finally {
      loading.value = false
    }
  }

  function redirectToLogin() {
    window.location.href = '/api/admin/me'
  }

  return {
    user,
    loading,
    authenticated,
    checkAuth,
    redirectToLogin,
  }
}
