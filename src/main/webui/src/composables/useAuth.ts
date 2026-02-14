import { ref } from 'vue'
import { getApiAdminMe } from '@/api/generated/admin-auth/admin-auth'
import type { UserInfoResponse } from '@/api/generated/model'

const user = ref<UserInfoResponse | null>(null)
const loading = ref(false)
const authenticated = ref(false)

export function useAuth() {
  async function checkAuth(): Promise<boolean> {
    if (authenticated.value) return true

    loading.value = true
    try {
      const response = await getApiAdminMe()
      user.value = response.data.data
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
