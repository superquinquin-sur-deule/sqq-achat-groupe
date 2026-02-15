import { ref, computed } from 'vue'

export function useCursorPagination() {
  const currentCursor = ref<string | null>(null)
  const cursorStack = ref<string[]>([])

  const hasPrevious = computed(() => cursorStack.value.length > 0)
  const pageNumber = computed(() => cursorStack.value.length + 1)

  function goToNext(endCursor: string) {
    if (currentCursor.value !== null) {
      cursorStack.value = [...cursorStack.value, currentCursor.value]
    } else {
      cursorStack.value = [...cursorStack.value, '']
    }
    currentCursor.value = endCursor
  }

  function goToPrevious() {
    if (cursorStack.value.length === 0) return
    const stack = [...cursorStack.value]
    const prev = stack.pop()!
    cursorStack.value = stack
    currentCursor.value = prev === '' ? null : prev
  }

  function reset() {
    currentCursor.value = null
    cursorStack.value = []
  }

  return {
    currentCursor,
    cursorStack,
    hasPrevious,
    pageNumber,
    goToNext,
    goToPrevious,
    reset,
  }
}
