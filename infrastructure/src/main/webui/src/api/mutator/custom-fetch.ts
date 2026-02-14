export interface ProblemDetail {
  type: string
  title: string
  status: number
  detail?: string
  instance?: string
}

export class ApiError extends Error {
  constructor(
    public readonly problem: ProblemDetail,
    public readonly status: number,
  ) {
    super(problem.detail || problem.title)
    this.name = 'ApiError'
  }
}

export const customFetch = async <T>(url: string, init: RequestInit): Promise<T> => {
  const requestHeaders: Record<string, string> = {
    Accept: 'application/json',
  }

  if (init.headers) {
    const headers = init.headers as Record<string, string>
    Object.entries(headers).forEach(([key, value]) => {
      requestHeaders[key] = value
    })
  }

  const response = await fetch(url, {
    ...init,
    headers: requestHeaders,
  })

  if (!response.ok) {
    const contentType = response.headers.get('content-type')
    if (
      contentType?.includes('application/problem+json') ||
      contentType?.includes('application/json')
    ) {
      const problem: ProblemDetail = await response.json()
      throw new ApiError(problem, response.status)
    }
    throw new ApiError(
      { type: 'about:blank', title: response.statusText, status: response.status },
      response.status,
    )
  }

  const data = response.status === 204 ? undefined : await response.json()

  return { data, status: response.status, headers: response.headers } as T
}

export default customFetch
