/**
 * Central configuration (mirrors coreit.planfit `src/config`).
 * Vite dev server proxies `/admin` → Spring Boot; production can set VITE_API_BASE.
 */
export interface CmsRuntimeConfig {
  /** Spring context path + origin prefix, e.g. `/admin` or `https://host/admin` */
  apiBaseUrl: string
  requestTimeoutMs: number
}

const DEFAULT_TIMEOUT_MS = 30_000

export function getCmsApiBaseUrl(): string {
  return import.meta.env.VITE_API_BASE ?? '/admin'
}

export function getCmsRuntimeConfig(): CmsRuntimeConfig {
  return {
    apiBaseUrl: getCmsApiBaseUrl(),
    requestTimeoutMs: Number(import.meta.env.VITE_API_TIMEOUT) || DEFAULT_TIMEOUT_MS,
  }
}
