import axios, { type AxiosError, type AxiosInstance } from 'axios'
import { getCmsRuntimeConfig } from '@/config'

let httpClientInstance: AxiosInstance | null = null

function toHttpError(error: unknown): Error {
  const err = error as AxiosError<{ message?: string; error?: string }>
  const data = err.response?.data
  const fromBody =
    data && typeof data === 'object'
      ? data.message ?? data.error
      : undefined
  const msg =
    (typeof fromBody === 'string' && fromBody) ||
    err.response?.statusText ||
    err.message ||
    'Request failed'
  return new Error(msg)
}

/**
 * Axios client for Spring Boot REST API under context-path `/admin`.
 * Pattern aligned with coreit.planfit `services/http-client.ts`.
 */
export function createHttpClient(): AxiosInstance {
  if (httpClientInstance) {
    return httpClientInstance
  }

  const { apiBaseUrl, requestTimeoutMs } = getCmsRuntimeConfig()

  const client = axios.create({
    baseURL: apiBaseUrl,
    timeout: requestTimeoutMs,
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
    },
  })

  client.interceptors.response.use(
    (res) => res,
    (error) => Promise.reject(toHttpError(error)),
  )

  httpClientInstance = client
  return client
}

export function getHttpClient(): AxiosInstance {
  if (!httpClientInstance) {
    throw new Error('HTTP client not initialized. Call initializeServices() from main.ts first.')
  }
  return httpClientInstance
}
