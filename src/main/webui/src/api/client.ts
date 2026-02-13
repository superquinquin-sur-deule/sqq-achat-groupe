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
    super(problem.detail ?? problem.title)
    this.name = 'ApiError'
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const contentType = response.headers.get('content-type')
    if (contentType?.includes('application/problem+json')) {
      const problem: ProblemDetail = await response.json()
      throw new ApiError(problem, response.status)
    }
    throw new ApiError(
      {
        type: 'about:blank',
        title: response.statusText,
        status: response.status,
        detail: await response.text(),
      },
      response.status,
    )
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json()
}

export const api = {
  async get<T>(path: string): Promise<T> {
    const response = await fetch(path, {
      headers: { Accept: 'application/json' },
    })
    return handleResponse<T>(response)
  },

  async post<T>(path: string, body?: unknown): Promise<T> {
    const response = await fetch(path, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: body ? JSON.stringify(body) : undefined,
    })
    return handleResponse<T>(response)
  },

  async put<T>(path: string, body?: unknown): Promise<T> {
    const response = await fetch(path, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: body ? JSON.stringify(body) : undefined,
    })
    return handleResponse<T>(response)
  },

  async delete<T>(path: string): Promise<T> {
    const response = await fetch(path, {
      method: 'DELETE',
      headers: { Accept: 'application/json' },
    })
    return handleResponse<T>(response)
  },
}
