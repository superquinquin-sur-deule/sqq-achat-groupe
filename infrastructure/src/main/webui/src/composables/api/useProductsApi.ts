import { computed, toValue, watch, type MaybeRefOrGetter } from 'vue'
import { useInfiniteQuery } from '@tanstack/vue-query'
import { queryKeys } from '@/api/queryKeys'
import { getApiVentesVenteIdProducts } from '@/api/generated/products/products'
import type { ProductResponse } from '@/api/generated/model'

export function useVenteProductsQuery(
  venteId: MaybeRefOrGetter<number>,
  enabled: MaybeRefOrGetter<boolean> = true,
) {
  const query = useInfiniteQuery({
    queryKey: computed(() => queryKeys.ventes.products(toValue(venteId))),
    queryFn: async ({ pageParam }: { pageParam: string | undefined }) => {
      const response = await getApiVentesVenteIdProducts(toValue(venteId), {
        cursor: pageParam,
        size: 50,
      })
      return response.data
    },
    initialPageParam: undefined as string | undefined,
    getNextPageParam: (lastPage) =>
      lastPage.pageInfo.hasNext ? lastPage.pageInfo.endCursor : undefined,
    enabled: () => toValue(enabled),
  })

  // Auto-fetch all remaining pages
  watch(
    [() => query.hasNextPage.value, () => query.isFetchingNextPage.value],
    ([hasNext, isFetching]) => {
      if (hasNext && !isFetching) {
        query.fetchNextPage()
      }
    },
  )

  const data = computed<ProductResponse[] | undefined>(() => {
    if (!query.data.value) return undefined
    return query.data.value.pages.flatMap((page) => page.data)
  })

  return {
    ...query,
    data,
    isFetchingNextPage: query.isFetchingNextPage,
  }
}
