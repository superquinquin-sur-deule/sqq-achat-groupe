import { ref } from 'vue'

export interface Toast {
  id: number
  message: string
  variant: 'success' | 'error' | 'info' | 'warning'
}

const toasts = ref<Toast[]>([])
let nextId = 0

export function useToast() {
  function show(message: string, variant: Toast['variant'] = 'info') {
    const id = nextId++
    toasts.value.push({ id, message, variant })
    return id
  }

  function remove(id: number) {
    toasts.value = toasts.value.filter((t) => t.id !== id)
  }

  function success(message: string) {
    return show(message, 'success')
  }

  function error(message: string) {
    return show(message, 'error')
  }

  function warning(message: string) {
    return show(message, 'warning')
  }

  function info(message: string) {
    return show(message, 'info')
  }

  return { toasts, show, remove, success, error, warning, info }
}
